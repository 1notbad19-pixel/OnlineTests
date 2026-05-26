import React, { useEffect, useState } from 'react';

interface Quiz {
  id: number;
  title: string;
  description: string;
  category: string;
  timeLimitMinutes: number;
  isPublished: boolean;
  tags: string[];
  questionCount: number;
}

interface QuizDetailsProps {
  quizId: number;
  onBack: () => void;
  onEdit: () => void;
  currentUserId?: number;
}

const QuizDetails: React.FC<QuizDetailsProps> = ({ quizId, onBack, onEdit, currentUserId }) => {
  const [quiz, setQuiz] = useState<Quiz | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetch(`http://localhost:8080/api/quizzes/${quizId}`)
      .then(res => res.json())
      .then(data => {
        setQuiz(data);
        setLoading(false);
      })
      .catch(err => {
        setError(err.message);
        setLoading(false);
      });
  }, [quizId]);

  if (loading) return <div style={{ padding: 50, textAlign: 'center' }}>Загрузка...</div>;
  if (error) return <div style={{ padding: 50, textAlign: 'center', color: 'red' }}>Ошибка: {error}</div>;
  if (!quiz) return <div>Квиз не найден</div>;

  const isCreator = currentUserId && quiz.createdById === currentUserId;

  return (
    <div style={{ maxWidth: 1000, margin: '0 auto', padding: 20 }}>
      <button onClick={onBack} style={{ marginBottom: 20, padding: '8px 16px', cursor: 'pointer' }}>← Назад</button>
      <div style={{ background: 'white', borderRadius: 8, padding: 30 }}>
        <h1>{quiz.title}</h1>
        <p>{quiz.description}</p>
        <hr />
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 15, marginBottom: 30 }}>
          <div><strong>Категория:</strong> {quiz.category}</div>
          <div><strong>Время:</strong> {quiz.timeLimitMinutes} мин</div>
          <div><strong>Статус:</strong> {quiz.isPublished ? 'Опубликован' : 'Черновик'}</div>
          <div><strong>Вопросов:</strong> {quiz.questionCount || 0}</div>
        </div>
        <div style={{ marginBottom: 20 }}>
          <strong>Теги:</strong>
          <div style={{ marginTop: 8 }}>
            {quiz.tags?.map(tag => <span key={tag} style={{ background: '#e9ecef', padding: '4px 12px', borderRadius: 16, fontSize: 14, marginRight: 8 }}>#{tag}</span>)}
          </div>
        </div>
        {isCreator && (
          <button onClick={onEdit} style={{ padding: '10px 20px', background: '#ffc107', border: 'none', borderRadius: 4, cursor: 'pointer' }}>
            ✏ Редактировать
          </button>
        )}
      </div>
    </div>
  );
};

export default QuizDetails;