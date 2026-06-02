import React from 'react';
import { useTheme } from '../context/ThemeContext';

interface UserProfileProps {
  user: {
    id: number;
    username: string;
    email: string;
    firstName?: string;
    lastName?: string;
    createdAt?: string;
  } | null;
}

const UserProfile: React.FC<UserProfileProps> = ({ user }) => {
  const { theme } = useTheme();

  if (!user) return <div style={{ padding: 50, textAlign: 'center', color: 'var(--text-primary)' }}>Нет данных</div>;

  return (
    <div style={{ maxWidth: 800, margin: '0 auto', padding: 30 }}>
      <div className="google-card" style={{
        backgroundColor: 'var(--bg-card)',
        borderRadius: 16,
        padding: 32,
        border: '1px solid var(--border-color)'
      }}>
        <h1 style={{ marginBottom: 30, fontSize: 28, fontWeight: 500, color: 'var(--text-primary)' }}>👤 Мой профиль</h1>

        <div style={{ marginBottom: 24 }}>
          <label style={{ fontWeight: 500, display: 'block', marginBottom: 8, color: 'var(--text-secondary)' }}>Имя пользователя</label>
          <div style={{
            padding: '12px 16px',
            backgroundColor: 'var(--bg-hover)',
            borderRadius: 12,
            color: 'var(--text-primary)',
            border: '1px solid var(--border-light)'
          }}>
            {user.username}
          </div>
        </div>

        <div style={{ marginBottom: 24 }}>
          <label style={{ fontWeight: 500, display: 'block', marginBottom: 8, color: 'var(--text-secondary)' }}>Email</label>
          <div style={{
            padding: '12px 16px',
            backgroundColor: 'var(--bg-hover)',
            borderRadius: 12,
            color: 'var(--text-primary)',
            border: '1px solid var(--border-light)'
          }}>
            {user.email}
          </div>
        </div>

        {user.firstName && (
          <div style={{ marginBottom: 24 }}>
            <label style={{ fontWeight: 500, display: 'block', marginBottom: 8, color: 'var(--text-secondary)' }}>Имя</label>
            <div style={{
              padding: '12px 16px',
              backgroundColor: 'var(--bg-hover)',
              borderRadius: 12,
              color: 'var(--text-primary)',
              border: '1px solid var(--border-light)'
            }}>
              {user.firstName} {user.lastName}
            </div>
          </div>
        )}

        {user.createdAt && (
          <div style={{ marginBottom: 24 }}>
            <label style={{ fontWeight: 500, display: 'block', marginBottom: 8, color: 'var(--text-secondary)' }}>Дата регистрации</label>
            <div style={{
              padding: '12px 16px',
              backgroundColor: 'var(--bg-hover)',
              borderRadius: 12,
              color: 'var(--text-primary)',
              border: '1px solid var(--border-light)'
            }}>
              {new Date(user.createdAt).toLocaleDateString('ru-RU', {
                year: 'numeric',
                month: 'long',
                day: 'numeric'
              })}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default UserProfile;