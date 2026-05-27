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
  createdByUsername?: string;
}

interface FavoritesProps {
  onTakeQuiz?: (id: number) => void;
  favorites?: number[];
  onRemoveFromFavorites?: (id: number) => void;
}

const Favorites: React.FC<FavoritesProps> = ({ onTakeQuiz, favorites = [], onRemoveFromFavorites }) => {
  const { theme } = useTheme();
  const [quizzes, setQuizzes] = useState<Quiz[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState<{ id: number; title: string } | null>(null);

  useEffect(() => {
    loadFavorites();
  }, [favorites]);

  const loadFavorites = async () => {
    setLoading(true);
    try {
      const response = await fetch('http://localhost:8080/api/quizzes');
      const data = await response.json();
      const favoriteQuizzes = data.filter((q: Quiz) => favorites.includes(q.id));
      setQuizzes(favoriteQuizzes);
    } catch (err) {
      setError('Ошибка загрузки избранного');
    } finally {
      setLoading(false);
    }
  };

  const handleRemoveClick = (id: number, title: string) => {
    setDeleteTarget({ id, title });
    setModalOpen(true);
  };

  const handleConfirmRemove = () => {
    if (deleteTarget && onRemoveFromFavorites) {
      onRemoveFromFavorites(deleteTarget.id);
      setQuizzes(quizzes.filter(q => q.id !== deleteTarget.id));
    }
    setModalOpen(false);
    setDeleteTarget(null);
  };

  const handleCancelRemove = () => {
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

  const categoryStats = quizzes.reduce((acc, quiz) => {
    acc[quiz.category] = (acc[quiz.category] || 0) + 1;
    return acc;
  }, {} as Record<string, number>);

  const popularCategories = Object.entries(categoryStats)
    .sort((a, b) => b[1] - a[1])
    .slice(0, 5);

  if (loading) return <div style={{ padding: 50, textAlign: 'center' }}>Загрузка избранного...</div>;
  if (error) return <div style={{ padding: 50, textAlign: 'center', color: 'var(--error-color)' }}>{error}</div>;

  return (
    <div style={{ maxWidth: 1200, margin: '0 auto', padding: 20 }}>
      <ConfirmModal
        isOpen={modalOpen}
        title="Удаление из избранного"
        message={`Вы действительно хотите удалить тест "${deleteTarget?.title}" из избранного?`}
        onConfirm={handleConfirmRemove}
        onCancel={handleCancelRemove}
      />

      <div style={{ marginBottom: 24 }}>
        <h1 style={{ fontSize: 28, fontWeight: 500, marginBottom: 8 }}>⭐ Избранное</h1>
        <p style={{ color: 'var(--text-secondary)' }}>
          {quizzes.length === 0
            ? 'У вас пока нет избранных тестов. Добавьте тест в избранное, нажав на звездочку ⭐ рядом с тестом.'
            : `У вас ${quizzes.length} ${quizzes.length === 1 ? 'избранный тест' : 'избранных тестов'}`}
        </p>
      </div>

      {quizzes.length > 0 && (
        <>
          {popularCategories.length > 0 && (
            <div className="google-card" style={{ padding: 20, marginBottom: 24 }}>
              <h3 style={{ fontSize: 16, fontWeight: 500, marginBottom: 12, color: 'var(--text-primary)' }}> Ваши любимые категории</h3>
              <div style={{ display: 'flex', gap: 12, flexWrap: 'wrap' }}>
                {popularCategories.map(([cat, count]) => (
                  <button
                    key={cat}
                    onClick={() => setCategoryFilter(cat)}
                    style={{
                      padding: '6px 14px',
                      borderRadius: 20,
                      backgroundColor: categoryFilter === cat ? 'var(--btn-primary)' : 'var(--bg-hover)',
                      color: categoryFilter === cat ? 'white' : 'var(--text-primary)',
                      border: 'none',
                      cursor: 'pointer',
                      fontSize: 13
                    }}
                  >
                    {cat} ({count})
                  </button>
                ))}
                {categoryFilter && (
                  <button
                    onClick={() => setCategoryFilter('')}
                    style={{ padding: '6px 14px', borderRadius: 20, backgroundColor: 'var(--btn-secondary)', color: 'white', border: 'none', cursor: 'pointer', fontSize: 13 }}
                  >
                    Сбросить ✕
                  </button>
                )}
              </div>
            </div>
          )}

          <div style={{ display: 'flex', gap: 16, marginBottom: 24, flexWrap: 'wrap' }}>
            <div style={{ flex: 1 }}>
              <input
                type="text"
                placeholder=" Поиск в избранном..."
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
        </>
      )}

      {filteredQuizzes.length === 0 && quizzes.length > 0 && (
        <div className="google-card" style={{ textAlign: 'center', padding: 60 }}>
          <div style={{ fontSize: 48, marginBottom: 16 }}></div>
          <h3>Ничего не найдено</h3>
          <p style={{ color: 'var(--text-secondary)', marginTop: 8 }}>Попробуйте изменить параметры поиска или фильтрации</p>
          <button onClick={() => { setSearchTerm(''); setCategoryFilter(''); }} className="btn-primary" style={{ marginTop: 20, padding: '8px 24px' }}>
            Сбросить фильтры
          </button>
        </div>
      )}

      {quizzes.length === 0 && (
        <div className="google-card" style={{ textAlign: 'center', padding: 60 }}>
          <div style={{ fontSize: 48, marginBottom: 16 }}>⭐</div>
          <h3>Избранное пусто</h3>
          <p style={{ color: 'var(--text-secondary)', marginTop: 8 }}>
            Нажмите на звездочку ⭐ рядом с тестом, чтобы добавить его в избранное.
          </p>
        </div>
      )}

      <div className="quiz-grid">
        {filteredQuizzes.map((quiz) => (
          <div key={quiz.id} className="quiz-card" style={{ display: 'flex', flexDirection: 'column', minHeight: 320 }}>
            <div style={{ flex: 1 }}>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 12 }}>
                <h3 style={{ margin: 0, fontSize: 18, fontWeight: 600, color: 'var(--text-primary)' }}>{quiz.title}</h3>
                <button
                  onClick={() => handleRemoveClick(quiz.id, quiz.title)}
                  style={{ background: 'none', border: 'none', cursor: 'pointer', fontSize: 18, color: 'var(--text-secondary)', padding: 0 }}
                  title="Удалить из избранного"
                >

                </button>
              </div>

              <p style={{ color: 'var(--text-secondary)', marginBottom: 12, fontSize: 14, lineHeight: 1.4 }}>
                {quiz.description?.length > 100 ? quiz.description.substring(0, 100) + '...' : quiz.description || 'Нет описания'}
              </p>

              {/* Обводка для информации о тесте */}
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

            <div className="quiz-card-buttons" style={{ marginTop: 16 }}>
              {onTakeQuiz && (
                <button onClick={() => onTakeQuiz(quiz.id)} className="btn-take" style={{ padding: '8px 16px', fontSize: '13px' }}>
                  Пройти
                </button>
              )}
              <button onClick={() => handleRemoveClick(quiz.id, quiz.title)} className="btn-delete" style={{ padding: '8px 16px', fontSize: '13px' }}>
                Удалить
              </button>
            </div>
          </div>
        ))}
      </div>

      {filteredQuizzes.length > 0 && (
        <div style={{ textAlign: 'center', marginTop: 32, color: 'var(--text-secondary)', fontSize: 13 }}>
          Показано {filteredQuizzes.length} из {quizzes.length} избранных тестов
        </div>
      )}
    </div>
  );
};

export default Favorites;