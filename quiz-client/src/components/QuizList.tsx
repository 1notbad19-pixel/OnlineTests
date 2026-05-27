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

interface QuizListProps {
  onView: (id: number) => void;
  onEdit: (id: number) => void;
  onDelete: (id: number) => void;
  onTakeQuiz?: (id: number) => void;
  onAddToFavorites?: (id: number) => void;
  favorites?: number[];
}

const QuizList: React.FC<QuizListProps> = ({
  onView,
  onEdit,
  onDelete,
  onTakeQuiz,
  onAddToFavorites,
  favorites = []
}) => {
  const { theme } = useTheme();
  const [quizzes, setQuizzes] = useState<Quiz[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('');

  const loadQuizzes = () => {
    fetch('http://localhost:8080/api/quizzes')
      .then(res => res.json())
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

  const handleDelete = async (id: number) => {
    if (window.confirm('Вы уверены, что хотите удалить этот тест?')) {
      try {
        await fetch(`http://localhost:8080/api/quizzes/${id}`, { method: 'DELETE' });
        onDelete(id);
        loadQuizzes();
        alert('Тест успешно удален');
      } catch (error) {
        alert('Ошибка удаления');
      }
    }
  };

  const filteredQuizzes = quizzes.filter(quiz => {
    const matchesSearch = quiz.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
                          quiz.description?.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesCategory = !categoryFilter || quiz.category === categoryFilter;
    return matchesSearch && matchesCategory;
  });

  const categories = [...new Set(quizzes.map(q => q.category))];

  if (loading) return <div style={{ padding: 50, textAlign: 'center' }}>Загрузка...</div>;
  if (error) return <div style={{ padding: 50, textAlign: 'center', color: 'var(--error-color)' }}>Ошибка: {error}</div>;

  return (
    <div style={{ maxWidth: 1200, margin: '0 auto', padding: 20 }}>
      <h1 style={{ fontSize: 28, fontWeight: 500, marginBottom: 8 }}>📋 Все тесты</h1>
      <p style={{ color: 'var(--text-secondary)', marginBottom: 20 }}>Исследуйте тесты от всех пользователей</p>

      <div style={{ display: 'flex', gap: 16, marginBottom: 20, flexWrap: 'wrap' }}>
        <input
          type="text"
          placeholder="Поиск тестов..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="google-input"
          style={{ flex: 1, minWidth: 200 }}
        />
        <select
          value={categoryFilter}
          onChange={(e) => setCategoryFilter(e.target.value)}
          className="google-input"
          style={{ width: 180 }}
        >
          <option value="">Все категории</option>
          {categories.map(cat => <option key={cat} value={cat}>{cat}</option>)}
        </select>
      </div>

      {filteredQuizzes.length === 0 && (
        <div className="google-card" style={{ textAlign: 'center', padding: 50 }}>
          <p>Тесты не найдены</p>
        </div>
      )}

      {filteredQuizzes.map((quiz) => {
        const isFavorite = favorites.includes(quiz.id);

        return (
          <div key={quiz.id} className="quiz-card">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start', flexWrap: 'wrap', gap: 10 }}>
              <div style={{ flex: 1 }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 10, flexWrap: 'wrap' }}>
                  <h3 style={{ margin: 0, color: 'var(--text-primary)' }}>{quiz.title}</h3>
                  {onAddToFavorites && (
                    <button
                      onClick={() => onAddToFavorites(quiz.id)}
                      style={{ background: 'none', border: 'none', cursor: 'pointer', fontSize: 20, color: isFavorite ? '#f29900' : 'var(--text-secondary)' }}
                    >
                      {isFavorite ? '⭐' : '☆'}
                    </button>
                  )}
                </div>
                <p style={{ color: 'var(--text-secondary)', margin: '10px 0' }}>{quiz.description}</p>
                <div style={{ display: 'flex', gap: 15, fontSize: 14, color: 'var(--text-secondary)', flexWrap: 'wrap' }}>
                  <span>📂 {quiz.category}</span>
                  <span>⏱️ {quiz.timeLimitMinutes} мин</span>
                  <span>📝 {quiz.questionCount} вопросов</span>
                  <span className={quiz.isPublished ? 'status-published' : 'status-draft'}>
                    {quiz.isPublished ? '✅ Опубликован' : '📝 Черновик'}
                  </span>
                </div>
                <div style={{ marginTop: 10 }}>
                  {quiz.tags?.map(tag => <span key={tag} className="tag">#{tag}</span>)}
                </div>
              </div>
              <div style={{ display: 'flex', gap: 10, flexWrap: 'wrap' }}>
                <button onClick={() => onView(quiz.id)} className="google-btn-primary" style={{ padding: '8px 20px', fontSize: '14px' }}>
                  Просмотр
                </button>
                {onTakeQuiz && (
                  <button onClick={() => onTakeQuiz(quiz.id)} className="google-btn-primary" style={{ padding: '8px 20px', fontSize: '14px' }}>
                    Пройти
                  </button>
                )}
                <button onClick={() => onEdit(quiz.id)} className="google-btn-warning" style={{ padding: '8px 20px', fontSize: '14px' }}>
                  Редактировать
                </button>
                <button onClick={() => handleDelete(quiz.id)} className="google-btn-danger" style={{ padding: '8px 20px', fontSize: '14px' }}>
                  Удалить
                </button>
              </div>
            </div>
          </div>
        );
      })}
    </div>
  );
};

export default QuizList;