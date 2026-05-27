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
  createdById?: number;
  createdByUsername?: string;
}

interface MyQuizzesProps {
  onView: (id: number) => void;
  onEdit: (id: number) => void;
  currentUserId?: number;
}

const MyQuizzes: React.FC<MyQuizzesProps> = ({ onView, onEdit, currentUserId }) => {
  const [quizzes, setQuizzes] = useState<Quiz[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filter, setFilter] = useState<'all' | 'published' | 'draft'>('all');

  useEffect(() => {
    loadQuizzes();
  }, [currentUserId]);

  const loadQuizzes = () => {
    setLoading(true);
    fetch('http://localhost:8080/api/quizzes')
      .then(res => res.json())
      .then(data => {
        // ФИЛЬТРАЦИЯ - оставляем только тесты текущего пользователя
       const myQuizzes = data.filter((q: Quiz) => q.createdById === currentUserId);
             setQuizzes(myQuizzes);
             setLoading(false);
           })
           .catch(err => {
             setError(err.message);
             setLoading(false);
           });
       };

  const getFilteredQuizzes = () => {
    if (filter === 'published') return quizzes.filter(q => q.isPublished === true);
    if (filter === 'draft') return quizzes.filter(q => q.isPublished === false);
    return quizzes;
  };

  const filteredQuizzes = getFilteredQuizzes();

  if (loading) {
    return <div style={{ padding: 50, textAlign: 'center' }}>Загрузка моих тестов...</div>;
  }

  if (error) {
    return <div style={{ padding: 50, textAlign: 'center', color: 'red' }}>Ошибка: {error}</div>;
  }

  return (
    <div style={{ maxWidth: 1200, margin: '0 auto', padding: 20 }}>
      <h1> Мои тесты</h1>
      <p>Всего создано: {quizzes.length}</p>

      <div style={{ display: 'flex', gap: 10, marginBottom: 20, flexWrap: 'wrap' }}>
        <button
          onClick={() => setFilter('all')}
          style={{
            padding: '8px 16px',
            backgroundColor: filter === 'all' ? '#007bff' : '#6c757d',
            color: 'white',
            border: 'none',
            borderRadius: 4,
            cursor: 'pointer'
          }}
        >
          Все ({quizzes.length})
        </button>
        <button
          onClick={() => setFilter('published')}
          style={{
            padding: '8px 16px',
            backgroundColor: filter === 'published' ? '#28a745' : '#6c757d',
            color: 'white',
            border: 'none',
            borderRadius: 4,
            cursor: 'pointer'
          }}
        >
          Опубликованные ({quizzes.filter(q => q.isPublished === true).length})
        </button>
        <button
          onClick={() => setFilter('draft')}
          style={{
            padding: '8px 16px',
            backgroundColor: filter === 'draft' ? '#ffc107' : '#6c757d',
            color: '#333',
            border: 'none',
            borderRadius: 4,
            cursor: 'pointer'
          }}
        >
          Черновики ({quizzes.filter(q => q.isPublished === false).length})
        </button>
      </div>

      {filteredQuizzes.length === 0 && (
        <div style={{ textAlign: 'center', padding: 50, backgroundColor: '#f5f5f5', borderRadius: 8 }}>
          <p>Нет тестов в этой категории</p>
          {filter === 'published' && <p>У вас нет опубликованных тестов. Чтобы опубликовать тест, отредактируйте его и поставьте галочку "Опубликовать".</p>}
          {filter === 'draft' && <p>У вас нет черновиков. Создайте новый тест, чтобы увидеть его здесь.</p>}
          {filter === 'all' && <p>У вас нет созданных тестов. Нажмите "Создать квиз" чтобы создать первый тест!</p>}
        </div>
      )}

      {filteredQuizzes.map((quiz) => (
        <div key={quiz.id} style={{ border: '1px solid #ddd', borderRadius: 8, padding: 15, marginBottom: 15, background: 'white' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start', flexWrap: 'wrap' }}>
            <div style={{ flex: 1 }}>
              <h3>{quiz.title}</h3>
              <p>{quiz.description}</p>
              <div style={{ display: 'flex', gap: 15, fontSize: 14, color: '#666', flexWrap: 'wrap' }}>
                <span> {quiz.category}</span>
                <span> {quiz.timeLimitMinutes} мин</span>
                <span> {quiz.questionCount} вопросов</span>
                <span style={{
                  backgroundColor: quiz.isPublished ? '#d4edda' : '#fff3cd',
                  color: quiz.isPublished ? '#155724' : '#856404',
                  padding: '2px 8px',
                  borderRadius: 12,
                  fontSize: 12
                }}>
                  {quiz.isPublished ? ' Опубликован' : ' Черновик'}
                </span>
              </div>
              <div style={{ marginTop: 10 }}>
                {quiz.tags?.map(tag => <span key={tag} style={{ background: '#e9ecef', padding: '2px 8px', borderRadius: 12, fontSize: 12, marginRight: 8 }}>#{tag}</span>)}
              </div>
              <div style={{ marginTop: 10, fontSize: 12, color: '#999' }}>
                ID автора: {quiz.createdById}
              </div>
            </div>
            <div style={{ display: 'flex', gap: 10, marginTop: 10 }}>
              <button
                onClick={() => onView(quiz.id)}
                style={{ padding: '6px 12px', background: '#28a745', color: 'white', border: 'none', borderRadius: 4, cursor: 'pointer' }}
              >
                Просмотр
              </button>
              <button
                onClick={() => onEdit(quiz.id)}
                style={{ padding: '6px 12px', background: '#ffc107', border: 'none', borderRadius: 4, cursor: 'pointer' }}
              >
                Редактировать
              </button>
            </div>
          </div>
        </div>
      ))}
    </div>
  );
};

export default MyQuizzes;