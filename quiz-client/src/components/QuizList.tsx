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

const QuizList: React.FC<{ onEdit: (id: number) => void; onView: (id: number) => void }> = ({ onEdit, onView }) => {
  const [quizzes, setQuizzes] = useState<Quiz[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const loadQuizzes = () => {
    fetch('/api/quizzes')
      .then(res => {
        if (!res.ok) throw new Error('Ошибка загрузки');
        return res.json();
      })
      .then(data => {
        setQuizzes(data);
        setLoading(false);
      })
      .catch(err => {
        setError(err.message);
        setLoading(false);
      });
  };

  useEffect(() => {
    loadQuizzes();
  }, []);

  if (loading) return <div style={{ padding: 50, textAlign: 'center' }}>Загрузка...</div>;
  if (error) return <div style={{ padding: 50, textAlign: 'center', color: 'red' }}>Ошибка: {error}</div>;

  return (
    <div style={{ maxWidth: 1200, margin: '0 auto', padding: 20 }}>
      <h1>Список квизов</h1>
      {quizzes.length === 0 && <p>Нет квизов. Создайте первый!</p>}
      {quizzes.map((quiz) => (
        <div key={quiz.id} style={{ border: '1px solid #ddd', borderRadius: 8, padding: 15, marginBottom: 15, background: 'white' }}>
          <h3>{quiz.title}</h3>
          <p>{quiz.description}</p>
          <div style={{ display: 'flex', gap: 15, fontSize: 14, color: '#666' }}>
            <span>📂 {quiz.category}</span>
            <span>⏱️ {quiz.timeLimitMinutes} мин</span>
            <span>📝 {quiz.questionCount} вопросов</span>
            <span>{quiz.isPublished ? '✅ Опубликован' : '📝 Черновик'}</span>
          </div>
          <div style={{ marginTop: 10 }}>
            {quiz.tags?.map(tag => <span key={tag} style={{ background: '#e9ecef', padding: '2px 8px', borderRadius: 12, fontSize: 12, marginRight: 8 }}>#{tag}</span>)}
          </div>
          <div style={{ marginTop: 15, display: 'flex', gap: 10 }}>
            <button onClick={() => onView(quiz.id)} style={{ padding: '6px 12px', background: '#28a745', color: 'white', border: 'none', borderRadius: 4, cursor: 'pointer' }}>Просмотр</button>
            <button onClick={() => onEdit(quiz.id)} style={{ padding: '6px 12px', background: '#ffc107', border: 'none', borderRadius: 4, cursor: 'pointer' }}>Редактировать</button>
          </div>
        </div>
      ))}
    </div>
  );
};

export default QuizList;