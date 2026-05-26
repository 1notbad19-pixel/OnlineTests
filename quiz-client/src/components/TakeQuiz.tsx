import React, { useEffect, useState } from 'react';

interface Question {
  id: number;
  text: string;
  type: string;
  points: number;
  answers: Answer[];
}

interface Answer {
  id: number;
  text: string;
  isCorrect: boolean;
}

interface Quiz {
  id: number;
  title: string;
  description: string;
  category: string;
  timeLimitMinutes: number;
  questionCount: number;
}

interface TakeQuizProps {
  quizId: number;
  onFinish: () => void;
}

const TakeQuiz: React.FC<TakeQuizProps> = ({ quizId, onFinish }) => {
  const [quiz, setQuiz] = useState<Quiz | null>(null);
  const [questions, setQuestions] = useState<Question[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
  const [selectedAnswers, setSelectedAnswers] = useState<{ [key: number]: number[] }>({});
  const [timeLeft, setTimeLeft] = useState<number | null>(null);
  const [finished, setFinished] = useState(false);
  const [score, setScore] = useState<{ correct: number; total: number; percentage: number } | null>(null);
  const [showConfirm, setShowConfirm] = useState(false);

  useEffect(() => {
    loadQuiz();
  }, [quizId]);

  const loadQuiz = async () => {
    try {
      const [quizRes, questionsRes] = await Promise.all([
        fetch(`http://localhost:8080/api/quizzes/${quizId}`),
        fetch(`http://localhost:8080/api/questions/quiz/${quizId}`)
      ]);

      const quizData = await quizRes.json();
      const questionsData = await questionsRes.json();

      setQuiz(quizData);
      setQuestions(questionsData);

      if (quizData.timeLimitMinutes) {
        setTimeLeft(quizData.timeLimitMinutes * 60);
      }

      setLoading(false);
    } catch (err) {
      setError('Ошибка загрузки теста');
      setLoading(false);
    }
  };

  useEffect(() => {
    if (timeLeft === null || timeLeft <= 0 || finished) return;

    const timer = setInterval(() => {
      setTimeLeft(prev => {
        if (prev === null || prev <= 1) {
          clearInterval(timer);
          finishQuiz();
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timer);
  }, [timeLeft, finished]);

  const handleAnswerSelect = (questionId: number, answerId: number, isMultiple: boolean) => {
    setSelectedAnswers(prev => {
      const current = prev[questionId] || [];

      if (isMultiple) {
        if (current.includes(answerId)) {
          return { ...prev, [questionId]: current.filter(id => id !== answerId) };
        } else {
          return { ...prev, [questionId]: [...current, answerId] };
        }
      } else {
        return { ...prev, [questionId]: [answerId] };
      }
    });
  };

  const isAnswerSelected = (questionId: number, answerId: number) => {
    return selectedAnswers[questionId]?.includes(answerId) || false;
  };

  const finishQuiz = () => {
    let correctCount = 0;
    let totalPoints = 0;
    let earnedPoints = 0;

    questions.forEach(question => {
      const selected = selectedAnswers[question.id] || [];
      const correctAnswers = question.answers.filter(a => a.isCorrect).map(a => a.id);
      totalPoints += question.points;

      let isCorrect = false;
      if (question.type === 'SINGLE') {
        isCorrect = selected.length === 1 && correctAnswers.includes(selected[0]);
      } else {
        isCorrect = selected.length === correctAnswers.length &&
                    selected.every(id => correctAnswers.includes(id));
      }

      if (isCorrect) {
        correctCount++;
        earnedPoints += question.points;
      }
    });

    const percentage = totalPoints > 0 ? Math.round((earnedPoints / totalPoints) * 100) : 0;
    setScore({ correct: correctCount, total: questions.length, percentage });
    setFinished(true);
  };

  const goToNextQuestion = () => {
    if (currentQuestionIndex < questions.length - 1) {
      setCurrentQuestionIndex(currentQuestionIndex + 1);
    } else {
      finishQuiz();
    }
  };

  const goToPreviousQuestion = () => {
    if (currentQuestionIndex > 0) {
      setCurrentQuestionIndex(currentQuestionIndex - 1);
    }
  };

  const handleCancel = () => {
    if (currentQuestionIndex > 0 || Object.keys(selectedAnswers).length > 0) {
      setShowConfirm(true);
    } else {
      onFinish();
    }
  };

  const confirmCancel = () => {
    setShowConfirm(false);
    onFinish();
  };

  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs < 10 ? '0' : ''}${secs}`;
  };

  if (loading) return <div style={{ padding: 50, textAlign: 'center' }}>Загрузка теста...</div>;
  if (error) return <div style={{ padding: 50, textAlign: 'center', color: 'red' }}>{error}</div>;
  if (!quiz) return <div>Тест не найден</div>;

  if (finished && score) {
    return (
      <div style={{ maxWidth: 600, margin: '50px auto', padding: 30, backgroundColor: 'white', borderRadius: 8, textAlign: 'center' }}>
        <h1>Результаты теста</h1>
        <h2>{quiz.title}</h2>
        <div style={{ fontSize: 48, margin: 30 }}>
          {score.percentage}%
        </div>
        <p>Правильных ответов: {score.correct} из {score.total}</p>
        <p>Набранные баллы: {score.percentage}%</p>
        {score.percentage >= 70 ? (
          <div style={{ color: 'green' }}> Поздравляем! Тест пройден!</div>
        ) : (
          <div style={{ color: 'red' }}> Тест не пройден. Попробуйте еще раз!</div>
        )}
        <button onClick={onFinish} style={{ marginTop: 30, padding: '10px 20px', backgroundColor: '#007bff', color: 'white', border: 'none', borderRadius: 4, cursor: 'pointer' }}>
          Закрыть
        </button>
      </div>
    );
  }

  const currentQuestion = questions[currentQuestionIndex];
  const isMultiple = currentQuestion?.type === 'MULTIPLE';
  const hasCurrentAnswer = selectedAnswers[currentQuestion?.id]?.length > 0;

  return (
    <div style={{ maxWidth: 800, margin: '0 auto', padding: 20 }}>
      {/* Модальное окно подтверждения */}
      {showConfirm && (
        <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000 }}>
          <div style={{ backgroundColor: 'white', borderRadius: 8, padding: 30, textAlign: 'center', maxWidth: 400 }}>
            <h3>Выйти из теста?</h3>
            <p>Весь прогресс будет потерян. Вы уверены?</p>
            <div style={{ display: 'flex', gap: 15, justifyContent: 'center', marginTop: 20 }}>
              <button onClick={() => setShowConfirm(false)} style={{ padding: '8px 20px', backgroundColor: '#6c757d', color: 'white', border: 'none', borderRadius: 4, cursor: 'pointer' }}>
                Продолжить
              </button>
              <button onClick={confirmCancel} style={{ padding: '8px 20px', backgroundColor: '#dc3545', color: 'white', border: 'none', borderRadius: 4, cursor: 'pointer' }}>
                Выйти
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20, padding: 15, backgroundColor: '#f5f5f5', borderRadius: 8 }}>
        <div>
          <h2>{quiz.title}</h2>
          <p>Вопрос {currentQuestionIndex + 1} из {questions.length}</p>
        </div>
        <div style={{ display: 'flex', gap: 15, alignItems: 'center' }}>
          {timeLeft !== null && (
            <div style={{ fontSize: 24, fontWeight: 'bold', color: timeLeft < 60 ? 'red' : '#333' }}>
              ⏱️ {formatTime(timeLeft)}
            </div>
          )}
          <button
            onClick={handleCancel}
            style={{
              padding: '8px 16px',
              backgroundColor: '#dc3545',
              color: 'white',
              border: 'none',
              borderRadius: 4,
              cursor: 'pointer',
              fontSize: 14
            }}
          >
            ✕ Выйти
          </button>
        </div>
      </div>

      {/* Progress bar */}
      <div style={{ width: '100%', height: 8, backgroundColor: '#e0e0e0', borderRadius: 4, marginBottom: 30 }}>
        <div style={{ width: `${((currentQuestionIndex + 1) / questions.length) * 100}%`, height: 8, backgroundColor: '#007bff', borderRadius: 4 }} />
      </div>

      {/* Question */}
      <div style={{ backgroundColor: 'white', borderRadius: 8, padding: 30, boxShadow: '0 2px 10px rgba(0,0,0,0.1)' }}>
        <h3>{currentQuestion?.text}</h3>
        <p style={{ color: '#666', fontSize: 14 }}>Тип: {isMultiple ? 'Множественный выбор' : 'Одиночный выбор'} | Баллов: {currentQuestion?.points}</p>

        <div style={{ marginTop: 30 }}>
          {currentQuestion?.answers.map((answer) => (
            <div
              key={answer.id}
              onClick={() => handleAnswerSelect(currentQuestion.id, answer.id, isMultiple)}
              style={{
                padding: 12,
                marginBottom: 10,
                backgroundColor: isAnswerSelected(currentQuestion.id, answer.id) ? '#e3f2fd' : '#f9f9f9',
                border: isAnswerSelected(currentQuestion.id, answer.id) ? '2px solid #007bff' : '1px solid #ddd',
                borderRadius: 8,
                cursor: 'pointer',
                transition: 'all 0.2s'
              }}
            >
              {isAnswerSelected(currentQuestion.id, answer.id) && (
                <span style={{ marginRight: 10, color: '#007bff' }}>✓</span>
              )}
              {answer.text}
            </div>
          ))}
        </div>

        {/* Navigation buttons */}
        <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 30 }}>
          <button
            onClick={goToPreviousQuestion}
            disabled={currentQuestionIndex === 0}
            style={{
              padding: '10px 20px',
              backgroundColor: currentQuestionIndex === 0 ? '#ccc' : '#6c757d',
              color: 'white',
              border: 'none',
              borderRadius: 4,
              cursor: currentQuestionIndex === 0 ? 'not-allowed' : 'pointer'
            }}
          >
            ← Назад
          </button>

          <button
            onClick={goToNextQuestion}
            disabled={!hasCurrentAnswer}
            style={{
              padding: '10px 20px',
              backgroundColor: !hasCurrentAnswer ? '#ccc' : (currentQuestionIndex === questions.length - 1 ? '#28a745' : '#007bff'),
              color: 'white',
              border: 'none',
              borderRadius: 4,
              cursor: !hasCurrentAnswer ? 'not-allowed' : 'pointer'
            }}
          >
            {currentQuestionIndex === questions.length - 1 ? 'Завершить' : 'Далее →'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default TakeQuiz;