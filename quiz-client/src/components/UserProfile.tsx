import React from 'react';

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
  if (!user) return <div>Нет данных</div>;

  return (
    <div style={{ maxWidth: 800, margin: '0 auto', padding: 30 }}>
      <div style={{ backgroundColor: 'white', borderRadius: 8, padding: 30, boxShadow: '0 2px 10px rgba(0,0,0,0.1)' }}>
        <h1 style={{ marginBottom: 30 }}> Мой профиль</h1>

        <div style={{ marginBottom: 20 }}>
          <label style={{ fontWeight: 'bold', display: 'block', marginBottom: 5 }}>Имя пользователя</label>
          <div style={{ padding: 10, backgroundColor: '#f5f5f5', borderRadius: 4 }}>{user.username}</div>
        </div>

        <div style={{ marginBottom: 20 }}>
          <label style={{ fontWeight: 'bold', display: 'block', marginBottom: 5 }}>Email</label>
          <div style={{ padding: 10, backgroundColor: '#f5f5f5', borderRadius: 4 }}>{user.email}</div>
        </div>

        {user.firstName && (
          <div style={{ marginBottom: 20 }}>
            <label style={{ fontWeight: 'bold', display: 'block', marginBottom: 5 }}>Имя</label>
            <div style={{ padding: 10, backgroundColor: '#f5f5f5', borderRadius: 4 }}>{user.firstName}</div>
          </div>
        )}

        {user.lastName && (
          <div style={{ marginBottom: 20 }}>
            <label style={{ fontWeight: 'bold', display: 'block', marginBottom: 5 }}>Фамилия</label>
            <div style={{ padding: 10, backgroundColor: '#f5f5f5', borderRadius: 4 }}>{user.lastName}</div>
          </div>
        )}

        {user.createdAt && (
          <div style={{ marginBottom: 20 }}>
            <label style={{ fontWeight: 'bold', display: 'block', marginBottom: 5 }}>Дата регистрации</label>
            <div style={{ padding: 10, backgroundColor: '#f5f5f5', borderRadius: 4 }}>{new Date(user.createdAt).toLocaleDateString()}</div>
          </div>
        )}
      </div>
    </div>
  );
};

export default UserProfile;