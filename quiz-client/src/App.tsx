import React, { useState, useEffect } from 'react';
import { useTheme } from './context/ThemeContext';
import QuizList from './components/QuizList';
import QuizForm from './components/QuizForm';
import QuizDetails from './components/QuizDetails';
import UserProfile from './components/UserProfile';
import TakeQuiz from './components/TakeQuiz';
import Favorites from './components/Favorites';
import Statistics from './components/Statistics';
import Rating from './components/Rating';
import MyResults from './components/MyResults';

type View = 'list' | 'create' | 'edit' | 'details' | 'profile' | 'favorites' | 'stats' | 'rating' | 'myresults';
type AuthView = 'login' | 'register';

function App() {
  const { theme, toggleTheme } = useTheme();
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [authView, setAuthView] = useState<AuthView>('login');
  const [currentView, setCurrentView] = useState<View>('list');
  const [selectedQuizId, setSelectedQuizId] = useState<number | undefined>();
  const [takingQuizId, setTakingQuizId] = useState<number | null>(null);
  const [user, setUser] = useState<any>(null);
  const [favorites, setFavorites] = useState<number[]>([]);

  useEffect(() => {
    const token = localStorage.getItem('token');
    const savedUser = localStorage.getItem('user');
    if (token && savedUser) {
      setIsAuthenticated(true);
      setUser(JSON.parse(savedUser));
    }
    const savedFavorites = localStorage.getItem('favorites');
    if (savedFavorites) {
      setFavorites(JSON.parse(savedFavorites));
    }
  }, []);

  useEffect(() => {
    localStorage.setItem('favorites', JSON.stringify(favorites));
  }, [favorites]);

  const handleLogin = async (username: string, password: string) => {
    try {
      const res = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
      });
      if (!res.ok) throw new Error('Login failed');
      const data = await res.json();
      localStorage.setItem('token', data.token);
      localStorage.setItem('user', JSON.stringify(data));
      setIsAuthenticated(true);
      setUser(data);
    } catch (error) {
      throw error;
    }
  };

  const handleRegister = async (userData: any) => {
    try {
      const res = await fetch('http://localhost:8080/api/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(userData)
      });
      if (!res.ok) {
        const error = await res.json();
        throw new Error(error.error || 'Registration failed');
      }
      const data = await res.json();
      localStorage.setItem('token', data.token);
      localStorage.setItem('user', JSON.stringify(data));
      setIsAuthenticated(true);
      setUser(data);
    } catch (error) {
      throw error;
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setIsAuthenticated(false);
    setUser(null);
    setCurrentView('list');
  };

  const handleEdit = (id: number) => {
    setSelectedQuizId(id);
    setCurrentView('edit');
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('Вы уверены, что хотите удалить этот тест?')) {
      try {
        await fetch(`http://localhost:8080/api/quizzes/${id}`, { method: 'DELETE' });
        window.location.reload();
      } catch (error) {
        alert('Ошибка удаления');
      }
    }
  };

  const handleView = (id: number) => {
    setSelectedQuizId(id);
    setCurrentView('details');
  };

  const handleTakeQuiz = (id: number) => {
    setTakingQuizId(id);
  };

  const handleFinishQuiz = () => {
    setTakingQuizId(null);
  };

  const handleBack = () => {
    setCurrentView('list');
    setSelectedQuizId(undefined);
  };

  const handleSuccess = () => {
    setCurrentView('list');
    setSelectedQuizId(undefined);
  };

  const handleAddToFavorites = (id: number) => {
    if (favorites.includes(id)) {
      setFavorites(favorites.filter(f => f !== id));
    } else {
      setFavorites([...favorites, id]);
    }
  };

  if (!isAuthenticated) {
    return (
      <div className="fade-in" style={{ maxWidth: 480, margin: '100px auto', padding: 32 }}>
        <div className="google-card" style={{ padding: 32 }}>
          <h1 style={{ textAlign: 'center', marginBottom: 32, fontSize: 28, fontWeight: 500 }}>
            {authView === 'login' ? 'Вход' : 'Регистрация'}
          </h1>
          {authView === 'login' ? (
            <LoginForm onLogin={handleLogin} onSwitch={() => setAuthView('register')} />
          ) : (
            <RegisterForm onRegister={handleRegister} onSwitch={() => setAuthView('login')} />
          )}
        </div>
      </div>
    );
  }

  if (takingQuizId) {
    return <TakeQuiz quizId={takingQuizId} onFinish={handleFinishQuiz} />;
  }

  return (
    <div className="fade-in">
      <nav className="google-navbar">
        <div style={{ maxWidth: 1280, margin: '0 auto', padding: '12px 20px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '12px' }}>
          <h2 style={{ margin: 0, cursor: 'pointer', fontSize: 22, fontWeight: 'bold', fontFamily: 'Arial, sans-serif' }} onClick={() => setCurrentView('list')}>
             Online Tests
          </h2>

          <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap', alignItems: 'center' }}>
            <button onClick={() => setCurrentView('list')} className={`google-btn ${currentView === 'list' ? 'google-btn-primary' : 'google-btn-secondary'}`} style={{ padding: '8px 16px' }}>
              Все тесты
            </button>
            <button onClick={() => setCurrentView('favorites')} className={`google-btn ${currentView === 'favorites' ? 'google-btn-primary' : 'google-btn-secondary'}`} style={{ padding: '8px 16px' }}>
              Избранное
            </button>
            <button onClick={() => setCurrentView('stats')} className={`google-btn ${currentView === 'stats' ? 'google-btn-primary' : 'google-btn-secondary'}`} style={{ padding: '8px 16px' }}>
              Статистика
            </button>
            <button onClick={() => setCurrentView('rating')} className={`google-btn ${currentView === 'rating' ? 'google-btn-primary' : 'google-btn-secondary'}`} style={{ padding: '8px 16px' }}>
              Рейтинг
            </button>
            <button onClick={() => setCurrentView('myresults')} className={`google-btn ${currentView === 'myresults' ? 'google-btn-primary' : 'google-btn-secondary'}`} style={{ padding: '8px 16px' }}>
              Мои результаты
            </button>
          </div>

          <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
            <button onClick={toggleTheme} className="google-btn-secondary" style={{ padding: '8px 12px', fontSize: 18 }}>
              {theme === 'light' ? '🌙' : '☀️'}
            </button>
            <button onClick={() => setCurrentView('profile')} className="google-btn-secondary" style={{ padding: '8px 16px', display: 'flex', alignItems: 'center', gap: '6px' }}>
              👤 {user?.username}
            </button>
            <button onClick={() => setCurrentView('create')} className="google-btn-primary" style={{ padding: '8px 20px' }}>
              + Создать
            </button>
            <button onClick={handleLogout} className="google-btn-danger" style={{ padding: '8px 16px' }}>
              Выйти
            </button>
          </div>
        </div>
      </nav>

      {currentView === 'list' && (
        <QuizList
          onView={handleView}
          onEdit={handleEdit}
          onDelete={handleDelete}
          onTakeQuiz={handleTakeQuiz}
          onAddToFavorites={handleAddToFavorites}
          favorites={favorites}
          currentUserId={user?.id}
        />
      )}
      {currentView === 'favorites' && (
        <Favorites
          currentUserId={user?.id}
          onTakeQuiz={handleTakeQuiz}
          favorites={favorites}
          onRemoveFromFavorites={handleAddToFavorites}
        />
      )}
      {currentView === 'stats' && <Statistics />}
      {currentView === 'rating' && <Rating />}
      {currentView === 'myresults' && <MyResults currentUserId={user?.id} />}
      {currentView === 'profile' && <UserProfile user={user} />}
      {currentView === 'create' && <QuizForm onSuccess={handleSuccess} onCancel={handleBack} />}
      {currentView === 'edit' && selectedQuizId && <QuizForm quizId={selectedQuizId} onSuccess={handleSuccess} onCancel={handleBack} />}
      {currentView === 'details' && selectedQuizId && (
        <QuizDetails
          quizId={selectedQuizId}
          onBack={handleBack}
          onEdit={() => handleEdit(selectedQuizId)}
          currentUserId={user?.id}
        />
      )}
    </div>
  );
}

// Компонент логина
function LoginForm({ onLogin, onSwitch }: { onLogin: (username: string, password: string) => Promise<void>; onSwitch: () => void }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await onLogin(username, password);
    } catch (err) {
      setError('Неверное имя пользователя или пароль');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      {error && <div style={{ color: 'var(--error-color)', padding: '12px', backgroundColor: 'rgba(217,48,37,0.1)', borderRadius: 8, marginBottom: 20 }}>{error}</div>}
      <div style={{ marginBottom: 16 }}>
        <input type="text" placeholder="Имя пользователя" value={username} onChange={e => setUsername(e.target.value)} required className="google-input" />
      </div>
      <div style={{ marginBottom: 24 }}>
        <input type="password" placeholder="Пароль" value={password} onChange={e => setPassword(e.target.value)} required className="google-input" />
      </div>
      <button type="submit" disabled={loading} className="google-btn google-btn-primary" style={{ width: '100%', padding: '12px' }}>
        {loading ? 'Вход...' : 'Войти'}
      </button>
      <div style={{ textAlign: 'center', marginTop: 20 }}>
        <button type="button" onClick={onSwitch} style={{ background: 'none', border: 'none', color: 'var(--primary-color)', cursor: 'pointer' }}>
          Нет аккаунта? Зарегистрироваться
        </button>
      </div>
    </form>
  );
}

// Компонент регистрации
function RegisterForm({ onRegister, onSwitch }: { onRegister: (data: any) => Promise<void>; onSwitch: () => void }) {
  const [formData, setFormData] = useState({ username: '', email: '', password: '', confirmPassword: '', firstName: '', lastName: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (formData.password !== formData.confirmPassword) {
      setError('Пароли не совпадают');
      return;
    }
    setError('');
    setLoading(true);
    try {
      const { confirmPassword, ...registerData } = formData;
      await onRegister(registerData);
    } catch (err: any) {
      setError(err.message || 'Ошибка регистрации');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      {error && <div style={{ color: 'var(--error-color)', padding: '12px', backgroundColor: 'rgba(217,48,37,0.1)', borderRadius: 8, marginBottom: 20 }}>{error}</div>}
      <div style={{ marginBottom: 16 }}>
        <input name="username" placeholder="Имя пользователя" value={formData.username} onChange={e => setFormData({ ...formData, username: e.target.value })} required className="google-input" />
      </div>
      <div style={{ marginBottom: 16 }}>
        <input name="email" type="email" placeholder="Email" value={formData.email} onChange={e => setFormData({ ...formData, email: e.target.value })} required className="google-input" />
      </div>
      <div style={{ display: 'flex', gap: 12, marginBottom: 16 }}>
        <input name="firstName" placeholder="Имя" value={formData.firstName} onChange={e => setFormData({ ...formData, firstName: e.target.value })} className="google-input" />
        <input name="lastName" placeholder="Фамилия" value={formData.lastName} onChange={e => setFormData({ ...formData, lastName: e.target.value })} className="google-input" />
      </div>
      <div style={{ marginBottom: 16 }}>
        <input type="password" placeholder="Пароль" value={formData.password} onChange={e => setFormData({ ...formData, password: e.target.value })} required className="google-input" />
      </div>
      <div style={{ marginBottom: 24 }}>
        <input type="password" placeholder="Подтверждение пароля" value={formData.confirmPassword} onChange={e => setFormData({ ...formData, confirmPassword: e.target.value })} required className="google-input" />
      </div>
      <button type="submit" disabled={loading} className="google-btn google-btn-primary" style={{ width: '100%', padding: '12px' }}>
        {loading ? 'Регистрация...' : 'Зарегистрироваться'}
      </button>
      <div style={{ textAlign: 'center', marginTop: 20 }}>
        <button type="button" onClick={onSwitch} style={{ background: 'none', border: 'none', color: 'var(--primary-color)', cursor: 'pointer' }}>
          Уже есть аккаунт? Войти
        </button>
      </div>
    </form>
  );
}

export default App;