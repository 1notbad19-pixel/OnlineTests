import React, { useState, useEffect } from 'react';

interface Answer {
  id?: number;
  text: string;
  isCorrect: boolean;
}

interface Question {
  id?: number;
  text: string;
  type: string;
  points: number;
  answers: Answer[];
}

const QuestionManager: React.FC<{ quizId: number; onClose: () => void }> = ({ quizId, onClose }) => {
  const [questions, setQuestions] = useState<Question[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [currentQuestion, setCurrentQuestion] = useState<Question>({
    text: '',
    type: 'SINGLE',
    points: 1,
    answers: [{ text: '', isCorrect: false }]
  });

  useEffect(() => {
    loadQuestions();
  }, [quizId]);

  const loadQuestions = async () => {
    try {
      const response = await fetch(`http://localhost:8080/api/questions/quiz/${quizId}`);
      if (response.ok) {
        const data = await response.json();
        setQuestions(data);
      }
    } catch (error) {
      console.error('Ошибка загрузки вопросов:', error);
    } finally {
      setLoading(false);
    }
  };

  const addAnswer = () => {
    setCurrentQuestion({
      ...currentQuestion,
      answers: [...currentQuestion.answers, { text: '', isCorrect: false }]
    });
  };

  const updateAnswer = (index: number, field: keyof Answer, value: string | boolean) => {
    const newAnswers = [...currentQuestion.answers];
    newAnswers[index] = { ...newAnswers[index], [field]: value };

    if (field === 'isCorrect' && value === true && currentQuestion.type === 'SINGLE') {
      newAnswers.forEach((answer, i) => {
        if (i !== index) answer.isCorrect = false;
      });
    }

    setCurrentQuestion({ ...currentQuestion, answers: newAnswers });
  };

  const removeAnswer = (index: number) => {
    const newAnswers = currentQuestion.answers.filter((_, i) => i !== index);
    setCurrentQuestion({ ...currentQuestion, answers: newAnswers });
  };

  const saveQuestion = async () => {
    setError(null);

    if (!currentQuestion.text.trim()) {
      setError('Введите текст вопроса');
      return;
    }

    const hasCorrectAnswer = currentQuestion.answers.some(a => a.isCorrect);
    if (!hasCorrectAnswer) {
      setError('Выберите правильный ответ');
      return;
    }

    // Проверка что у всех ответов есть текст
    for (const answer of currentQuestion.answers) {
      if (!answer.text.trim()) {
        setError('Заполните текст всех ответов');
        return;
      }
    }

    try {
      const requestBody = {
        text: currentQuestion.text,
        type: currentQuestion.type,
        points: currentQuestion.points,
        quizId: quizId,
        answers: currentQuestion.answers.map(a => ({
          text: a.text,
          isCorrect: a.isCorrect
        }))
      };

      console.log('Sending question:', requestBody);

      const response = await fetch('http://localhost:8080/api/questions', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(requestBody)
      });

      if (!response.ok) {
        const errorData = await response.text();
        console.error('Error response:', errorData);
        setError('Ошибка сохранения вопроса: ' + response.status);
        return;
      }

      setShowForm(false);
      setCurrentQuestion({
        text: '',
        type: 'SINGLE',
        points: 1,
        answers: [{ text: '', isCorrect: false }]
      });
      loadQuestions();
    } catch (error) {
      console.error('Error:', error);
      setError('Ошибка сети');
    }
  };

  const deleteQuestion = async (questionId: number) => {
    if (confirm('Удалить вопрос?')) {
      try {
        await fetch(`http://localhost:8080/api/questions/${questionId}`, { method: 'DELETE' });
        loadQuestions();
      } catch (error) {
        console.error('Ошибка удаления:', error);
      }
    }
  };

  if (loading) return <div style={{ padding: 50, textAlign: 'center' }}>Загрузка вопросов...</div>;

  return (
    <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.5)', zIndex: 1000, overflow: 'auto' }}>
      <div style={{ maxWidth: 800, margin: '50px auto', backgroundColor: 'white', borderRadius: 8, padding: 30 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
          <h2>Управление вопросами</h2>
          <button onClick={onClose} style={{ fontSize: 24, cursor: 'pointer', background: 'none', border: 'none' }}>✕</button>
        </div>

        {error && <div style={{ color: 'red', padding: 10, backgroundColor: '#ffebee', borderRadius: 4, marginBottom: 20 }}>{error}</div>}

        <button onClick={() => setShowForm(!showForm)} style={{ marginBottom: 20, padding: '10px 20px', backgroundColor: '#28a745', color: 'white', border: 'none', borderRadius: 4, cursor: 'pointer' }}>
          {showForm ? 'Отмена' : '+ Добавить вопрос'}
        </button>

        {showForm && (
          <div style={{ border: '1px solid #ddd', padding: 20, borderRadius: 8, marginBottom: 20 }}>
            <h3>Новый вопрос</h3>

            <div style={{ marginBottom: 15 }}>
              <label style={{ display: 'block', marginBottom: 5, fontWeight: 'bold' }}>Текст вопроса *</label>
              <textarea
                value={currentQuestion.text}
                onChange={e => setCurrentQuestion({ ...currentQuestion, text: e.target.value })}
                rows={3}
                style={{ width: '100%', padding: 10, border: '1px solid #ddd', borderRadius: 4 }}
                placeholder="Введите текст вопроса"
              />
            </div>

            <div style={{ display: 'flex', gap: 15, marginBottom: 15 }}>
              <div style={{ flex: 1 }}>
                <label style={{ display: 'block', marginBottom: 5, fontWeight: 'bold' }}>Тип вопроса</label>
                <select
                  value={currentQuestion.type}
                  onChange={e => setCurrentQuestion({ ...currentQuestion, type: e.target.value })}
                  style={{ width: '100%', padding: 10, border: '1px solid #ddd', borderRadius: 4 }}
                >
                  <option value="SINGLE">Одиночный выбор</option>
                  <option value="MULTIPLE">Множественный выбор</option>
                </select>
              </div>
              <div style={{ flex: 1 }}>
                <label style={{ display: 'block', marginBottom: 5, fontWeight: 'bold' }}>Баллы</label>
                <input
                  type="number"
                  value={currentQuestion.points}
                  onChange={e => setCurrentQuestion({ ...currentQuestion, points: parseInt(e.target.value) })}
                  min={1}
                  style={{ width: '100%', padding: 10, border: '1px solid #ddd', borderRadius: 4 }}
                />
              </div>
            </div>

            <div style={{ marginBottom: 15 }}>
              <label style={{ display: 'block', marginBottom: 5, fontWeight: 'bold' }}>Варианты ответов</label>
              {currentQuestion.answers.map((answer, idx) => (
                <div key={idx} style={{ display: 'flex', gap: 10, marginBottom: 10, alignItems: 'center' }}>
                  <input
                    type="text"
                    value={answer.text}
                    onChange={e => updateAnswer(idx, 'text', e.target.value)}
                    placeholder={`Ответ ${idx + 1}`}
                    style={{ flex: 1, padding: 8, border: '1px solid #ddd', borderRadius: 4 }}
                  />
                  <label style={{ display: 'flex', alignItems: 'center', gap: 5 }}>
                    <input
                      type="checkbox"
                      checked={answer.isCorrect}
                      onChange={e => updateAnswer(idx, 'isCorrect', e.target.checked)}
                    />
                    Правильный
                  </label>
                  <button
                    onClick={() => removeAnswer(idx)}
                    style={{ padding: '8px 12px', backgroundColor: '#dc3545', color: 'white', border: 'none', borderRadius: 4, cursor: 'pointer' }}
                  >
                    ✕
                  </button>
                </div>
              ))}
              <button
                onClick={addAnswer}
                style={{ padding: '8px 16px', backgroundColor: '#6c757d', color: 'white', border: 'none', borderRadius: 4, cursor: 'pointer' }}
              >
                + Добавить ответ
              </button>
            </div>

            <button
              onClick={saveQuestion}
              style={{ padding: '10px 20px', backgroundColor: '#007bff', color: 'white', border: 'none', borderRadius: 4, cursor: 'pointer' }}
            >
              Сохранить вопрос
            </button>
          </div>
        )}

        <h3>Список вопросов ({questions.length})</h3>
        {questions.length === 0 && <p>Нет вопросов. Добавьте первый!</p>}
        {questions.map((q, idx) => (
          <div key={q.id} style={{ border: '1px solid #eee', borderRadius: 8, padding: 15, marginBottom: 15 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start' }}>
              <div>
                <strong>Вопрос {idx + 1}: {q.text}</strong>
                <span style={{ marginLeft: 10, color: '#666', fontSize: 12 }}>({q.type === 'SINGLE' ? 'Одиночный' : 'Множественный'}) - {q.points} баллов</span>
                <div style={{ marginTop: 10, marginLeft: 20 }}>
                  {q.answers?.map((a, aidx) => (
                    <div key={aidx} style={{ margin: '5px 0' }}>
                      {a.isCorrect ? '' : '○'} {a.text}
                    </div>
                  ))}
                </div>
              </div>
              <button
                onClick={() => q.id && deleteQuestion(q.id)}
                style={{ padding: '5px 10px', backgroundColor: '#dc3545', color: 'white', border: 'none', borderRadius: 4, cursor: 'pointer' }}
              >
                Удалить
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default QuestionManager;