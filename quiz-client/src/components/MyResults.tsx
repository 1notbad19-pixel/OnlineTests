import React, { useEffect, useState } from 'react';
import { useTheme } from '../context/ThemeContext';

interface MyResultsProps {
  currentUserId?: number;
}

const MyResults: React.FC<MyResultsProps> = ({ currentUserId }) => {
  const { theme } = useTheme();
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Функционал в разработке
    setLoading(false);
  }, [currentUserId]);

  if (loading) return <div style={{ padding: 50, textAlign: 'center' }}>Загрузка результатов...</div>;

  return (
    <div style={{ maxWidth: 1000, margin: '0 auto', padding: 20 }}>
      <h1 style={{ fontSize: 28, fontWeight: 500, marginBottom: 8 }}> Мои результаты</h1>

      <div style={{
        backgroundColor: 'var(--bg-card)',
        padding: 60,
        borderRadius: 12,
        textAlign: 'center',
        border: '1px solid var(--border-color)'
      }}>
        <div style={{ fontSize: 64, marginBottom: 20 }}></div>
        <h3 style={{ marginBottom: 12, fontSize: 24 }}>Функционал в разработке</h3>
        <p style={{ color: 'var(--text-secondary)', fontSize: 16 }}>
          Здесь будут отображаться результаты ваших пройденных тестов.<br />
          Эта функция будет добавлена в следующем обновлении.
        </p>
      </div>
    </div>
  );
};

export default MyResults;