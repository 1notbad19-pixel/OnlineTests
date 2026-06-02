import React, { useEffect, useState } from 'react';
import { useTheme } from '../context/ThemeContext';
import ConfirmModal from './ConfirmModal';

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
  const [modalOpen, setModalOpen] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState<{ id: number; title: string } | null>(null);

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

  const handleDeleteClick = (id: number, title: string) => {
    setDeleteTarget({ id, title });
    setModalOpen(true);
  };

  const handleConfirmDelete = async () => {
    if (deleteTarget) {
      try {
        await fetch(`http://localhost:8080/api/quizzes/${deleteTarget.id}`, { method: 'DELETE' });
        onDelete(deleteTarget.id);
        loadQuizzes();
      } catch (error) {
        console.error('Ошибка удаления:', error);
      }
    }
    setModalOpen(false);
    setDeleteTarget(null);
  };

  const handleCancelDelete = () => {
    setModalOpen(false);
    setDeleteTarget(null);
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
      <ConfirmModal
        isOpen={modalOpen}
        title="Удаление теста"
        message={`Вы действительно хотите удалить тест "${deleteTarget?.title}"? Это действие нельзя отменить.`}
        onConfirm={handleConfirmDelete}
        onCancel={handleCancelDelete}
      />

      <h1 style={{ fontSize: 28, fontWeight: 500, marginBottom: 8 }}> Все тесты</h1>
      <p style={{ color: 'var(--text-secondary)', marginBottom: 20 }}>Исследуйте тесты от всех пользователей</p>

      <div style={{ display: 'flex', gap: 16, marginBottom: 30, flexWrap: 'wrap' }}>
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

      <div className="quiz-grid">
        {filteredQuizzes.map((quiz) => {
          const isFavorite = favorites.includes(quiz.id);

          return (
            <div key={quiz.id} className="quiz-card">
              <div style={{ flex: 1 }}>
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 12 }}>
                  <h3 style={{ margin: 0, fontSize: 18, fontWeight: 600, color: 'var(--text-primary)' }}>{quiz.title}</h3>
                  {onAddToFavorites && (
                    <button
                      onClick={() => onAddToFavorites(quiz.id)}
                      style={{ background: 'none', border: 'none', cursor: 'pointer', fontSize: 22, color: isFavorite ? '#f29900' : 'var(--text-secondary)', padding: 0 }}
                    >
                      {isFavorite ? '⭐' : '☆'}
                    </button>
                  )}
                </div>

                <p style={{ color: 'var(--text-secondary)', marginBottom: 12, fontSize: 14, lineHeight: 1.4 }}>
                  {quiz.description?.length > 100 ? quiz.description.substring(0, 100) + '...' : quiz.description}
                </p>

                <div style={{
                  display: 'flex',
                  gap: 12,
                  fontSize: 13,
                  color: 'var(--text-secondary)',
                  flexWrap: 'wrap',
                  marginBottom: 12,
                  padding: '8px 12px',
                  backgroundColor: 'var(--bg-hover)',
                  borderRadius: 12,
                  border: '1px solid var(--border-light)'
                }}>
                  <span> {quiz.category}</span>
                  <span> {quiz.timeLimitMinutes} мин</span>
                  <span> {quiz.questionCount} вопросов</span>
                </div>

                <div style={{ marginBottom: 12 }}>
                  <span className={quiz.isPublished ? 'status-published' : 'status-draft'}>
                    {quiz.isPublished ? ' Опубликован' : ' Черновик'}
                  </span>
                </div>

                <div style={{ marginBottom: 16 }}>
                  {quiz.tags?.slice(0, 3).map(tag => <span key={tag} className="tag">#{tag}</span>)}
                  {quiz.tags?.length > 3 && <span className="tag">+{quiz.tags.length - 3}</span>}
                </div>
              </div>

              <div className="quiz-card-buttons">
                <button onClick={() => onView(quiz.id)} className="btn-view" style={{ padding: '8px 16px', fontSize: '13px' }}>
                  Просмотр
                </button>
                {onTakeQuiz && (
                  <button onClick={() => onTakeQuiz(quiz.id)} className="btn-take" style={{ padding: '8px 16px', fontSize: '13px' }}>
                    Пройти
                  </button>
                )}
                <button onClick={() => onEdit(quiz.id)} className="btn-edit" style={{ padding: '8px 16px', fontSize: '13px' }}>
                  Редактировать
                </button>
                <button onClick={() => handleDeleteClick(quiz.id, quiz.title)} className="btn-delete" style={{ padding: '8px 16px', fontSize: '13px' }}>
                  Удалить
                </button>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default QuizList;