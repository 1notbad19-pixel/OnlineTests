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
  createdById?: number;
  createdByUsername?: string;
}

interface QuizListProps {
  onView: (id: number) => void;
  onEdit: (id: number) => void;
  onDelete: (id: number) => void;
  onTakeQuiz?: (id: number) => void;
  onAddToFavorites?: (id: number) => void;
  onCreateQuiz?: () => void;
  favorites?: number[];
  currentUserId?: number;
}

const QuizList: React.FC<QuizListProps> = ({
  onView,
  onEdit,
  onDelete,
  onTakeQuiz,
  onAddToFavorites,
  onCreateQuiz,
  favorites = [],
  currentUserId
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
    fetch('https://onlinetests-production.up.railway.app/api/quizzes')
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
        await fetch(`https://onlinetests-production.up.railway.app/api/quizzes/${deleteTarget.id}`, { method: 'DELETE' });
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

  // Статистика по категориям
  const categoryStats = quizzes.reduce((acc, quiz) => {
    acc[quiz.category] = (acc[quiz.category] || 0) + 1;
    return acc;
  }, {} as Record<string, number>);

  const popularCategories = Object.entries(categoryStats)
    .sort((a, b) => b[1] - a[1])
    .slice(0, 5);

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

      {/* Заголовок и кнопка создания в одной строке */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20, flexWrap: 'wrap', gap: 16 }}>
        <div>
          <h1 style={{ fontSize: 28, fontWeight: 500, marginBottom: 8 }}>📋 Все тесты</h1>
          <p style={{ color: 'var(--text-secondary)' }}>
            Найдено тестов: <strong style={{ color: 'var(--primary-color)' }}>{filteredQuizzes.length}</strong> из {quizzes.length}
          </p>
        </div>
        {onCreateQuiz && (
          <button onClick={onCreateQuiz} className="google-btn-primary" style={{ padding: '10px 24px', fontSize: '14px' }}>
            + Создать тест
          </button>
        )}
      </div>

      {/* Блок статистики */}
      {popularCategories.length > 0 && (
        <div className="google-card" style={{ padding: 16, marginBottom: 20 }}>
          <h3 style={{ fontSize: 14, fontWeight: 500, marginBottom: 10, color: 'var(--text-secondary)' }}>📊 Популярные категории</h3>
          <div style={{ display: 'flex', gap: 10, flexWrap: 'wrap' }}>
            {popularCategories.map(([cat, count]) => (
              <button
                key={cat}
                onClick={() => setCategoryFilter(cat)}
                style={{
                  padding: '5px 12px',
                  borderRadius: 20,
                  backgroundColor: categoryFilter === cat ? 'var(--btn-primary)' : 'var(--bg-hover)',
                  color: categoryFilter === cat ? 'white' : 'var(--text-primary)',
                  border: 'none',
                  cursor: 'pointer',
                  fontSize: 12
                }}
              >
                {cat} ({count})
              </button>
            ))}
            {categoryFilter && (
              <button
                onClick={() => setCategoryFilter('')}
                style={{ padding: '5px 12px', borderRadius: 20, backgroundColor: 'var(--btn-secondary)', color: 'white', border: 'none', cursor: 'pointer', fontSize: 12 }}
              >
                Сбросить ✕
              </button>
            )}
          </div>
        </div>
      )}

      {/* Поиск и фильтры */}
      <div style={{ display: 'flex', gap: 16, marginBottom: 24, flexWrap: 'wrap' }}>
        <div style={{ flex: 1 }}>
          <input
            type="text"
            placeholder="🔍 Поиск тестов..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="google-input"
            style={{ width: '100%' }}
          />
        </div>
        <select
          value={categoryFilter}
          onChange={(e) => setCategoryFilter(e.target.value)}
          className="google-input"
          style={{ width: 160 }}
        >
          <option value="">Все категории</option>
          {categories.map(cat => <option key={cat} value={cat}>{cat}</option>)}
        </select>
      </div>

      {filteredQuizzes.length === 0 && (
        <div className="google-card" style={{ textAlign: 'center', padding: 60 }}>
          <div style={{ fontSize: 48, marginBottom: 16 }}>🔍</div>
          <h3>Ничего не найдено</h3>
          <p style={{ color: 'var(--text-secondary)', marginTop: 8 }}>Попробуйте изменить параметры поиска или фильтрации</p>
          <button onClick={() => { setSearchTerm(''); setCategoryFilter(''); }} className="google-btn-primary" style={{ marginTop: 20, padding: '8px 24px' }}>
            Сбросить фильтры
          </button>
        </div>
      )}

      {/* Сетка тестов */}
      <div className="quiz-grid">
        {filteredQuizzes.map((quiz) => {
          const isCreator = currentUserId && quiz.createdById === currentUserId;
          const isFavorite = favorites.includes(quiz.id);

          return (
            <div key={quiz.id} className="quiz-card" style={{ display: 'flex', flexDirection: 'column', minHeight: 340 }}>
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
                  {quiz.description?.length > 100 ? quiz.description.substring(0, 100) + '...' : quiz.description || 'Нет описания'}
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
                  <span>📂 {quiz.category}</span>
                  <span>⏱️ {quiz.timeLimitMinutes} мин</span>
                  <span>📝 {quiz.questionCount} вопросов</span>
                </div>

                <div style={{ marginBottom: 12 }}>
                  <span className={quiz.isPublished ? 'status-published' : 'status-draft'}>
                    {quiz.isPublished ? '✅ Опубликован' : '📝 Черновик'}
                  </span>
                </div>

                <div style={{ marginBottom: 16 }}>
                  {quiz.tags?.slice(0, 3).map(tag => <span key={tag} className="tag">#{tag}</span>)}
                  {quiz.tags?.length > 3 && <span className="tag">+{quiz.tags.length - 3}</span>}
                </div>

                {quiz.createdByUsername && (
                  <div style={{ fontSize: 11, color: 'var(--text-secondary)', marginTop: 8 }}>
                    👤 {quiz.createdByUsername}
                  </div>
                )}
              </div>

              <div className="quiz-card-buttons" style={{ marginTop: 16 }}>
                <button onClick={() => onView(quiz.id)} className="btn-view" style={{ padding: '8px 16px', fontSize: '13px' }}>
                  Просмотр
                </button>
                {onTakeQuiz && (
                  <button onClick={() => onTakeQuiz(quiz.id)} className="btn-view" style={{ padding: '8px 16px', fontSize: '13px' }}>
                    Пройти
                  </button>
                )}
                {isCreator && (
                  <>
                    <button onClick={() => onEdit(quiz.id)} className="btn-view" style={{ padding: '8px 16px', fontSize: '13px' }}>
                      Редактировать
                    </button>
                    <button onClick={() => handleDeleteClick(quiz.id, quiz.title)} className="btn-delete" style={{ padding: '8px 16px', fontSize: '13px' }}>
                      Удалить
                    </button>
                  </>
                )}
              </div>
            </div>
          );
        })}
      </div>

      {filteredQuizzes.length > 0 && (
        <div style={{ textAlign: 'center', marginTop: 32, color: 'var(--text-secondary)', fontSize: 13 }}>
          Показано {filteredQuizzes.length} из {quizzes.length} тестов
        </div>
      )}
    </div>
  );
};

export default QuizList;