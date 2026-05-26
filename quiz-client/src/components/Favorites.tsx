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

  useEffect(() => {
    fetch('http://localhost:8080/api/quizzes')
      .then(res => res.json())
      .then(data => {
        const favoriteQuizzes = data.filter((q: Quiz) => favorites.includes(q.id));
        setQuizzes(favoriteQuizzes);
        setLoading(false);
      })
      .catch(err => {
        setError(err.message);
        setLoading(false);
      });
  }, [favorites]);

  if (loading) return <div style={{ padding: 50, textAlign: 'center' }}>Загрузка...</div>;
  if (error) return <div style={{ padding: 50, textAlign: 'center', color: 'var(--error-color)' }}>Ошибка: {error}</div>;

  return (
    <div style={{ maxWidth: 1200, margin: '0 auto', padding: 20 }}>
      <h1 style={{ fontSize: 28, fontWeight: 500, marginBottom: 8 }}>⭐ Избранное</h1>
      <p style={{ color: 'var(--text-secondary)', marginBottom: 24 }}>Тесты, которые вы добавили в избранное</p>

      {quizzes.length === 0 && (
        <div style={{ textAlign: 'center', padding: 60, backgroundColor: 'var(--bg-card)', borderRadius: 12, border: '1px solid var(--border-color)' }}>
          <p>Нет избранных тестов.</p>
          <p style={{ color: 'var(--text-secondary)', marginTop: 8 }}>Нажмите на звездочку ⭐ рядом с тестом, чтобы добавить его в избранное.</p>
        </div>
      )}

      {quizzes.map((quiz) => (
        <div key={quiz.id} style={{
          backgroundColor: 'var(--bg-card)',
          border: '1px solid var(--border-color)',
          borderRadius: 12,
          padding: 20,
          marginBottom: 16
        }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start', flexWrap: 'wrap', gap: 16 }}>
            <div style={{ flex: 1 }}>
              <h3 style={{ marginBottom: 8, color: 'var(--text-primary)' }}>{quiz.title}</h3>
              <p style={{ color: 'var(--text-secondary)', marginBottom: 12 }}>{quiz.description}</p>
              <div style={{ display: 'flex', gap: 16, fontSize: 13, color: 'var(--text-secondary)', flexWrap: 'wrap' }}>
                <span> {quiz.category}</span>
                <span> {quiz.timeLimitMinutes} мин</span>
                <span> {quiz.questionCount} вопросов</span>
              </div>
              <div style={{ marginTop: 12 }}>
                {quiz.tags?.map(tag => <span key={tag} style={{ backgroundColor: 'var(--bg-hover)', color: 'var(--text-secondary)', padding: '4px 12px', borderRadius: 16, fontSize: 12, marginRight: 8 }}>#{tag}</span>)}
              </div>
            </div>
            <div style={{ display: 'flex', gap: 8 }}>
              {onTakeQuiz && (
                <button onClick={() => onTakeQuiz(quiz.id)} className="google-btn-primary" style={{ padding: '8px 16px' }}>
                  Пройти
                </button>
              )}
              {onRemoveFromFavorites && (
                <button onClick={() => onRemoveFromFavorites(quiz.id)} className="google-btn-danger" style={{ padding: '8px 16px' }}>
                  Удалить
                </button>
              )}
            </div>
          </div>
        </div>
      ))}
    </div>
  );
};

export default Favorites;