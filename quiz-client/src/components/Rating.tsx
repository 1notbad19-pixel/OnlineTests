import React, { useEffect, useState } from 'react';
import { useTheme } from '../context/ThemeContext';

interface UserRating {
  id: number;
  username: string;
  quizzesCount: number;
}

const Rating: React.FC = () => {
  const { theme } = useTheme();
  const [users, setUsers] = useState<UserRating[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    Promise.all([
      fetch('http://localhost:8080/api/users').then(res => res.json()),
      fetch('http://localhost:8080/api/quizzes').then(res => res.json())
    ])
      .then(([usersData, quizzes]) => {
        const userQuizCount = new Map<number, number>();
        quizzes.forEach((q: any) => {
          if (q.createdById) {
            userQuizCount.set(q.createdById, (userQuizCount.get(q.createdById) || 0) + 1);
          }
        });

        const usersWithRating = usersData.map((user: any) => ({
          id: user.id,
          username: user.username,
          quizzesCount: userQuizCount.get(user.id) || 0
        })).sort((a, b) => b.quizzesCount - a.quizzesCount);

        setUsers(usersWithRating);
        setLoading(false);
      })
      .catch(err => {
        setError(err.message);
        setLoading(false);
      });
  }, []);

  if (loading) return <div style={{ padding: 50, textAlign: 'center' }}>Загрузка рейтинга...</div>;
  if (error) return <div style={{ padding: 50, textAlign: 'center', color: 'var(--error-color)' }}>Ошибка: {error}</div>;

  return (
    <div style={{ maxWidth: 800, margin: '0 auto', padding: 20 }}>
      <h1 style={{ fontSize: 28, fontWeight: 500, marginBottom: 8 }}> Рейтинг создателей тестов</h1>
      <p style={{ color: 'var(--text-secondary)', marginBottom: 24 }}>Топ пользователей по количеству созданных квизов</p>

      <div style={{ backgroundColor: 'var(--bg-card)', borderRadius: 8, overflow: 'hidden', border: '1px solid var(--border-color)' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
          <thead style={{ backgroundColor: '#343a40', color: 'white' }}>
            <tr>
              <th style={{ padding: 12, textAlign: 'center' }}>#</th>
              <th style={{ padding: 12, textAlign: 'left' }}>Пользователь</th>
              <th style={{ padding: 12, textAlign: 'center' }}>Создано квизов</th>
              <th style={{ padding: 12, textAlign: 'center' }}>Рейтинг</th>
            </tr>
          </thead>
          <tbody>
            {users.map((user, index) => (
              <tr key={user.id} style={{ borderBottom: '1px solid var(--border-color)' }}>
                <td style={{ padding: 12, textAlign: 'center', fontWeight: 'bold' }}>
                  {index === 0 && '1'}
                  {index === 1 && '2'}
                  {index === 2 && '3'}
                  {index > 2 && index + 1}
                </td>
                <td style={{ padding: 12, color: 'var(--text-primary)' }}>👤 {user.username}</td>
                <td style={{ padding: 12, textAlign: 'center', color: 'var(--text-primary)' }}>{user.quizzesCount}</td>
                <td style={{ padding: 12, textAlign: 'center' }}>
                  <div style={{
                    width: `${(user.quizzesCount / (users[0]?.quizzesCount || 1)) * 100}%`,
                    height: 8,
                    backgroundColor: '#ffc107',
                    borderRadius: 4,
                    maxWidth: 100
                  }} />
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default Rating;