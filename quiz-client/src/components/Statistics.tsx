import React, { useEffect, useState } from 'react';
import { useTheme } from '../context/ThemeContext';

interface Stat {
  totalQuizzes: number;
  totalQuestions: number;
  totalUsers: number;
  publishedQuizzes: number;
  categories: { name: string; count: number }[];
}

const Statistics: React.FC = () => {
  const { theme } = useTheme();
  const [stats, setStats] = useState<Stat | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    Promise.all([
      fetch('http://localhost:8080/api/quizzes').then(res => res.json()),
      fetch('http://localhost:8080/api/users').then(res => res.json())
    ])
      .then(([quizzes, users]) => {
        const totalQuizzes = quizzes.length;
        const totalUsers = users.length;
        const publishedQuizzes = quizzes.filter((q: any) => q.isPublished).length;

        const categoryMap = new Map<string, number>();
        quizzes.forEach((q: any) => {
          categoryMap.set(q.category, (categoryMap.get(q.category) || 0) + 1);
        });
        const categories = Array.from(categoryMap.entries()).map(([name, count]) => ({ name, count }));

        let totalQuestions = 0;
        Promise.all(quizzes.map((q: any) =>
          fetch(`http://localhost:8080/api/questions/quiz/${q.id}`).then(res => res.json())
        )).then(questionsArrays => {
          totalQuestions = questionsArrays.reduce((sum, arr) => sum + arr.length, 0);
          setStats({
            totalQuizzes,
            totalQuestions,
            totalUsers,
            publishedQuizzes,
            categories
          });
          setLoading(false);
        });
      })
      .catch(err => {
        setError(err.message);
        setLoading(false);
      });
  }, []);

  if (loading) return <div style={{ padding: 50, textAlign: 'center' }}>Загрузка статистики...</div>;
  if (error) return <div style={{ padding: 50, textAlign: 'center', color: 'var(--error-color)' }}>Ошибка: {error}</div>;
  if (!stats) return null;

  return (
    <div style={{ maxWidth: 1200, margin: '0 auto', padding: 20 }}>
      <h1 style={{ fontSize: 28, fontWeight: 500, marginBottom: 8 }}> Статистика платформы</h1>
      <p style={{ color: 'var(--text-secondary)', marginBottom: 24 }}>Общая информация о платформе</p>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 20, marginBottom: 40 }}>
        <div style={{ backgroundColor: 'var(--bg-card)', padding: 20, borderRadius: 8, textAlign: 'center', border: '1px solid var(--border-color)' }}>
          <div style={{ fontSize: 36, fontWeight: 'bold', color: 'var(--primary-color)' }}>{stats.totalQuizzes}</div>
          <div style={{ color: 'var(--text-secondary)' }}>Всего квизов</div>
        </div>
        <div style={{ backgroundColor: 'var(--bg-card)', padding: 20, borderRadius: 8, textAlign: 'center', border: '1px solid var(--border-color)' }}>
          <div style={{ fontSize: 36, fontWeight: 'bold', color: 'var(--primary-color)' }}>{stats.totalQuestions}</div>
          <div style={{ color: 'var(--text-secondary)' }}>Всего вопросов</div>
        </div>
        <div style={{ backgroundColor: 'var(--bg-card)', padding: 20, borderRadius: 8, textAlign: 'center', border: '1px solid var(--border-color)' }}>
          <div style={{ fontSize: 36, fontWeight: 'bold', color: 'var(--primary-color)' }}>{stats.totalUsers}</div>
          <div style={{ color: 'var(--text-secondary)' }}>Пользователей</div>
        </div>
        <div style={{ backgroundColor: 'var(--bg-card)', padding: 20, borderRadius: 8, textAlign: 'center', border: '1px solid var(--border-color)' }}>
          <div style={{ fontSize: 36, fontWeight: 'bold', color: 'var(--primary-color)' }}>{stats.publishedQuizzes}</div>
          <div style={{ color: 'var(--text-secondary)' }}>Опубликовано</div>
        </div>
      </div>

      <h2 style={{ marginBottom: 16 }}> Распределение по категориям</h2>
      <div style={{ display: 'flex', flexWrap: 'wrap', gap: 15 }}>
        {stats.categories.map(cat => (
          <div key={cat.name} style={{ flex: 1, minWidth: 150, backgroundColor: 'var(--bg-card)', padding: 15, borderRadius: 8, border: '1px solid var(--border-color)' }}>
            <div style={{ fontWeight: 'bold' }}>{cat.name}</div>
            <div style={{ fontSize: 24, color: 'var(--primary-color)' }}>{cat.count}</div>
            <div style={{ fontSize: 12, color: 'var(--text-secondary)' }}>квизов</div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Statistics;