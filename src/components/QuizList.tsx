import React, { useEffect, useState } from 'react';
import { quizApi } from '../services/api';
import { Quiz } from '../types';
import FilterBar from './FilterBar';

interface QuizListProps {
  onEdit?: (id: number) => void;
  onView?: (id: number) => void;
}

const QuizList: React.FC<QuizListProps> = ({ onEdit, onView }) => {
  const [quizzes, setQuizzes] = useState<Quiz[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [category, setCategory] = useState('');
  const [published, setPublished] = useState('');
  const [minQuestions, setMinQuestions] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const loadQuizzes = async () => {
    setLoading(true);
    try {
      const response = await quizApi.getFilters(
        page, 10,
        category || undefined,
        published === 'true' ? true : published === 'false' ? false : undefined,
        minQuestions ? parseInt(minQuestions) : undefined
      );
      setQuizzes(response.data.content);
      setTotalPages(response.data.totalPages);
      setError(null);
    } catch (err) {
      setError('Ошибка загрузки квизов');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadQuizzes();
  }, [page, category, published, minQuestions]);

  const handleDelete = async (id: number) => {
    if (window.confirm('Удалить квиз?')) {
      try {
        await quizApi.delete(id);
        loadQuizzes();
      } catch (err) {
        setError('Ошибка удаления');
      }
    }
  };

  const handleReset = () => {
    setCategory('');
    setPublished('');
    setMinQuestions('');
    setPage(0);
  };

  if (loading) return <div style={{ textAlign: 'center', padding: '50px' }}>Загрузка...</div>;

  return (
    <div style={{ maxWidth: '1200px', margin: '0 auto', padding: '20px' }}>
      <h1 style={{ marginBottom: '20px' }}>📚 Управление квизами</h1>

      <FilterBar
        category={category}
        setCategory={setCategory}
        published={published}
        setPublished={setPublished}
        minQuestions={minQuestions}
        setMinQuestions={setMinQuestions}
        onApply={() => setPage(0)}
        onReset={handleReset}
      />

      {error && <div style={{ color: 'red', padding: '10px', backgroundColor: '#ffebee', borderRadius: '4px', marginBottom: '20px' }}>{error}</div>}

      <div style={{ display: 'grid', gap: '15px' }}>
        {quizzes.map((quiz) => (
          <div key={quiz.id} style={{ border: '1px solid #ddd', borderRadius: '8px', padding: '15px', backgroundColor: 'white' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start' }}>
              <div>
                <h3 style={{ margin: '0 0 10px 0' }}>{quiz.title}</h3>
                <p style={{ color: '#666', margin: '5px 0' }}>{quiz.description}</p>
                <div style={{ display: 'flex', gap: '15px', marginTop: '10px', fontSize: '14px', color: '#888' }}>
                  <span>📂 {quiz.category}</span>
                  <span>⏱️ {quiz.timeLimitMinutes} мин</span>
                  <span>📝 {quiz.questionCount} вопросов</span>
                  <span>{quiz.isPublished ? '✅ Опубликован' : '📝 Черновик'}</span>
                </div>
                <div style={{ marginTop: '8px' }}>
                  {quiz.tags?.map((tag) => (
                    <span key={tag} style={{ backgroundColor: '#e9ecef', padding: '2px 8px', borderRadius: '12px', fontSize: '12px', marginRight: '8px' }}>#{tag}</span>
                  ))}
                </div>
              </div>
              <div style={{ display: 'flex', gap: '10px' }}>
                <button onClick={() => onView?.(quiz.id)} style={{ padding: '6px 12px', backgroundColor: '#28a745', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Просмотр</button>
                <button onClick={() => onEdit?.(quiz.id)} style={{ padding: '6px 12px', backgroundColor: '#ffc107', color: '#333', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Редактировать</button>
                <button onClick={() => handleDelete(quiz.id)} style={{ padding: '6px 12px', backgroundColor: '#dc3545', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Удалить</button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {totalPages > 0 && (
        <div style={{ display: 'flex', justifyContent: 'center', gap: '10px', marginTop: '30px' }}>
          <button disabled={page === 0} onClick={() => setPage(page - 1)} style={{ padding: '8px 16px', cursor: 'pointer' }}>◀ Назад</button>
          <span style={{ padding: '8px 16px' }}>Страница {page + 1} из {totalPages}</span>
          <button disabled={page >= totalPages - 1} onClick={() => setPage(page + 1)} style={{ padding: '8px 16px', cursor: 'pointer' }}>Вперед ▶</button>
        </div>
      )}
    </div>
  );
};

export default QuizList;