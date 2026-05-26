import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';

interface RegisterProps {
  onSwitchToLogin: () => void;
}

const Register: React.FC<RegisterProps> = ({ onSwitchToLogin }) => {
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
    firstName: '',
    lastName: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { register } = useAuth();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (formData.password !== formData.confirmPassword) {
      setError('Пароли не совпадают');
      return;
    }

    setLoading(true);

    try {
      const { confirmPassword, ...registerData } = formData;
      await register(registerData);
    } catch (err) {
      setError('Ошибка регистрации. Возможно, имя пользователя уже занято.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: 500, margin: '50px auto', padding: 30, backgroundColor: 'white', borderRadius: 8, boxShadow: '0 2px 10px rgba(0,0,0,0.1)' }}>
      <h1 style={{ textAlign: 'center', marginBottom: 30 }}> Регистрация</h1>

      {error && <div style={{ color: 'red', padding: 10, backgroundColor: '#ffebee', borderRadius: 4, marginBottom: 20 }}>{error}</div>}

      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: 15 }}>
          <label style={{ display: 'block', marginBottom: 5, fontWeight: 'bold' }}>Имя пользователя *</label>
          <input name="username" value={formData.username} onChange={handleChange} required style={{ width: '100%', padding: 10, border: '1px solid #ddd', borderRadius: 4 }} />
        </div>

        <div style={{ marginBottom: 15 }}>
          <label style={{ display: 'block', marginBottom: 5, fontWeight: 'bold' }}>Email *</label>
          <input name="email" type="email" value={formData.email} onChange={handleChange} required style={{ width: '100%', padding: 10, border: '1px solid #ddd', borderRadius: 4 }} />
        </div>

        <div style={{ display: 'flex', gap: 15, marginBottom: 15 }}>
          <div style={{ flex: 1 }}>
            <label style={{ display: 'block', marginBottom: 5, fontWeight: 'bold' }}>Имя</label>
            <input name="firstName" value={formData.firstName} onChange={handleChange} style={{ width: '100%', padding: 10, border: '1px solid #ddd', borderRadius: 4 }} />
          </div>
          <div style={{ flex: 1 }}>
            <label style={{ display: 'block', marginBottom: 5, fontWeight: 'bold' }}>Фамилия</label>
            <input name="lastName" value={formData.lastName} onChange={handleChange} style={{ width: '100%', padding: 10, border: '1px solid #ddd', borderRadius: 4 }} />
          </div>
        </div>

        <div style={{ marginBottom: 15 }}>
          <label style={{ display: 'block', marginBottom: 5, fontWeight: 'bold' }}>Пароль *</label>
          <input name="password" type="password" value={formData.password} onChange={handleChange} required style={{ width: '100%', padding: 10, border: '1px solid #ddd', borderRadius: 4 }} />
        </div>

        <div style={{ marginBottom: 20 }}>
          <label style={{ display: 'block', marginBottom: 5, fontWeight: 'bold' }}>Подтверждение пароля *</label>
          <input name="confirmPassword" type="password" value={formData.confirmPassword} onChange={handleChange} required style={{ width: '100%', padding: 10, border: '1px solid #ddd', borderRadius: 4 }} />
        </div>

        <button type="submit" disabled={loading} style={{ width: '100%', padding: 12, backgroundColor: '#28a745', color: 'white', border: 'none', borderRadius: 4, cursor: 'pointer', fontSize: 16 }}>
          {loading ? 'Регистрация...' : 'Зарегистрироваться'}
        </button>
      </form>

      <div style={{ textAlign: 'center', marginTop: 20 }}>
        <button onClick={onSwitchToLogin} style={{ background: 'none', border: 'none', color: '#007bff', cursor: 'pointer' }}>
          Уже есть аккаунт? Войти
        </button>
      </div>
    </div>
  );
};

export default Register;