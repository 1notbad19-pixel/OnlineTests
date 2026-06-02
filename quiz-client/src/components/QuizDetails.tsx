import React, { useEffect, useState } from 'react';
import { useTheme } from '../context/ThemeContext';

interface Quiz {
  id: number;
  title: string;
  description: string;
  category: string;
  timeLimitMinutes: number;
  isPublished: boolean;
  tags: string[];
  questionCount: number;
  createdById?: number;
  createdByUsername?: string;
}

interface QuizDetailsProps {
  quizId: number;
  onBack: () => void;
  onEdit: () => void;
  currentUserId?: number;
}

const QuizDetails: React.FC<QuizDetailsProps> = ({ quizId, onBack, onEdit, currentUserId }) => {
  const { theme } = useTheme();
  const [quiz, setQuiz] = useState<Quiz | null>(null);
  const [questions, setQuestions] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadQuizDetails();
  }, [quizId]);

  const loadQuizDetails = async () => {
    try {
      const [quizRes, questionsRes] = await Promise.all([
        fetch(`http://localhost:8080/api/quizzes/${quizId}`),
        fetch(`http://localhost:8080/api/questions/quiz/${quizId}`)
      ]);

      const quizData = await quizRes.json();
      const questionsData = await questionsRes.json();

      setQuiz(quizData);
      setQuestions(questionsData);
      setLoading(false);
    } catch (err) {
      setError('Ошибка загрузки');
      setLoading(false);
    }
  };

  if (loading) return <div style={{ padding: 50, textAlign: 'center' }}>Загрузка...</div>;
  if (error) return <div style={{ padding: 50, textAlign: 'center', color: 'var(--error-color)' }}>Ошибка: {error}</div>;
  if (!quiz) return <div>Квиз не найден</div>;

  const isCreator = currentUserId && quiz.createdById === currentUserId;

  return (
    <div style={{ maxWidth: 1000, margin: '0 auto', padding: 20 }}>
      <button onClick={onBack} className="google-btn-secondary" style={{ marginBottom: 20, padding: '8px 16px' }}>
        ← Назад
      </button>

      <div className="google-card" style={{ padding: 30, marginBottom: 30 }}>
        <h1 style={{ marginBottom: 16, fontSize: 28 }}>{quiz.title}</h1>
        <p style={{ color: 'var(--text-secondary)', marginBottom: 20 }}>{quiz.description}</p>

        <hr style={{ margin: '20px 0', borderColor: 'var(--border-color)' }} />

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: 16, marginBottom: 30 }}>
          <div>
            <strong>Категория:</strong>
            <span style={{ marginLeft: 8, color: 'var(--text-secondary)' }}>{quiz.category}</span>
          </div>
          <div>
            <strong>Время:</strong>
            <span style={{ marginLeft: 8, color: 'var(--text-secondary)' }}>{quiz.timeLimitMinutes} мин</span>
          </div>
          <div>
            <strong>Статус:</strong>
            <span style={{ marginLeft: 8, color: quiz.isPublished ? 'var(--success-color)' : 'var(--warning-color)' }}>
              {quiz.isPublished ? ' Опубликован' : ' Черновик'}
            </span>
          </div>
          <div>
            <strong>Вопросов:</strong>
            <span style={{ marginLeft: 8, color: 'var(--text-secondary)' }}>{quiz.questionCount || questions.length}</span>
          </div>
        </div>

        {quiz.tags && quiz.tags.length > 0 && (
          <div style={{ marginBottom: 20 }}>
            <strong>Теги:</strong>
            <div style={{ marginTop: 8 }}>
              {quiz.tags.map(tag => (
                <span key={tag} className="tag" style={{ marginRight: 8 }}>#{tag}</span>
              ))}
            </div>
          </div>
        )}

        {isCreator && (
          <button onClick={onEdit} className="google-btn-warning" style={{ padding: '10px 20px' }}>
            ✏ Редактировать
          </button>
        )}
      </div>

      {questions.length > 0 && (
        <div className="google-card" style={{ padding: 30 }}>
          <h2 style={{ marginBottom: 20 }}>Вопросы ({questions.length})</h2>
          {questions.map((q, idx) => (
            <div key={q.id} style={{
              borderBottom: idx !== questions.length - 1 ? '1px solid var(--border-color)' : 'none',
              paddingBottom: idx !== questions.length - 1 ? 20 : 0,
              marginBottom: idx !== questions.length - 1 ? 20 : 0
            }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12 }}>
                <strong style={{ fontSize: 16 }}>Вопрос {idx + 1}: {q.text}</strong>
                <span style={{ color: 'var(--text-secondary)', fontSize: 14 }}>{q.points} баллов</span>
              </div>
              <div style={{ marginLeft: 24 }}>
                {q.answers?.map((a: any) => (
                  <div key={a.id} style={{ margin: '8px 0', display: 'flex', alignItems: 'center', gap: 10 }}>
                    {a.isCorrect ? <span style={{ color: 'var(--success-color)' }}></span> : <span style={{ color: 'var(--text-secondary)' }}>○</span>}
                    <span style={{ color: 'var(--text-secondary)' }}>{a.text}</span>
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default QuizDetails;