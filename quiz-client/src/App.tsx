import React, { useEffect, useState } from 'react';

function App() {
  const [quizzes, setQuizzes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    category: '',
    timeLimitMinutes: 30,
    maxAttempts: 3,
    isPublished: true,
    passingScore: 70,
    tags: ''
  });

  // Загрузка квизов
  const loadQuizzes = () => {
    setLoading(true);
    fetch('http://localhost:8080/api/quizzes')
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

  // Создание квиза
  const createQuiz = (e) => {
    e.preventDefault();
    const payload = {
      ...formData,
      tags: formData.tags.split(',').map(t => t.trim()).filter(t => t)
    };

    fetch('http://localhost:8080/api/quizzes', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
      .then(res => res.json())
      .then(() => {
        setShowForm(false);
        setFormData({
          title: '', description: '', category: '', timeLimitMinutes: 30,
          maxAttempts: 3, isPublished: true, passingScore: 70, tags: ''
        });
        loadQuizzes();
      })
      .catch(err => alert('Ошибка: ' + err.message));
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div style={{ padding: 20 }}>
      <h1>Online Test</h1>
      <button onClick={() => setShowForm(!showForm)} style={{ marginBottom: 20, padding: '10px 20px', cursor: 'pointer' }}>
        {showForm ? 'Отмена' : '+ Создать квиз'}
      </button>

      {showForm && (
        <form onSubmit={createQuiz} style={{ border: '1px solid #ccc', padding: 20, borderRadius: 8, marginBottom: 20 }}>
          <h2>Новый квиз</h2>
          <div style={{ marginBottom: 10 }}>
            <input name="title" placeholder="Название *" value={formData.title} onChange={handleChange} required style={{ width: '100%', padding: 8 }} />
          </div>
          <div style={{ marginBottom: 10 }}>
            <input name="description" placeholder="Описание" value={formData.description} onChange={handleChange} style={{ width: '100%', padding: 8 }} />
          </div>
          <div style={{ marginBottom: 10 }}>
            <input name="category" placeholder="Категория *" value={formData.category} onChange={handleChange} required style={{ width: '100%', padding: 8 }} />
          </div>
          <div style={{ display: 'flex', gap: 10, marginBottom: 10 }}>
            <input name="timeLimitMinutes" type="number" placeholder="Время (мин)" value={formData.timeLimitMinutes} onChange={handleChange} style={{ width: '33%', padding: 8 }} />
            <input name="maxAttempts" type="number" placeholder="Макс попыток" value={formData.maxAttempts} onChange={handleChange} style={{ width: '33%', padding: 8 }} />
            <input name="passingScore" type="number" placeholder="Проходной балл" value={formData.passingScore} onChange={handleChange} style={{ width: '33%', padding: 8 }} />
          </div>
          <div style={{ marginBottom: 10 }}>
            <input name="tags" placeholder="Теги (через запятую)" value={formData.tags} onChange={handleChange} style={{ width: '100%', padding: 8 }} />
          </div>
          <div style={{ marginBottom: 10 }}>
            <label>
              <input name="isPublished" type="checkbox" checked={formData.isPublished} onChange={(e) => setFormData({ ...formData, isPublished: e.target.checked })} />
              Опубликовать
            </label>
          </div>
          <button type="submit" style={{ padding: '10px 20px', backgroundColor: '#28a745', color: 'white', border: 'none', borderRadius: 4, cursor: 'pointer' }}>Сохранить</button>
        </form>
      )}

      <h2>Список квизов ({quizzes.length})</h2>
      {quizzes.length === 0 && <p>Нет квизов. Создайте первый!</p>}
      {quizzes.map(quiz => (
        <div key={quiz.id} style={{ border: '1px solid #ddd', padding: 15, marginBottom: 10, borderRadius: 8 }}>
          <h3>{quiz.title}</h3>
          <p>{quiz.description}</p>
          <p>Категория: {quiz.category} | Время: {quiz.timeLimitMinutes} мин | Вопросов: {quiz.questionCount || 0}</p>
          {quiz.tags?.map(tag => <span key={tag} style={{ background: '#eee', padding: '2px 8px', borderRadius: 12, marginRight: 8 }}>#{tag}</span>)}
        </div>
      ))}
    </div>
  );
}

export default App;