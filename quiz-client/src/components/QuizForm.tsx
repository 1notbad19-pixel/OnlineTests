import React, { useState, useEffect } from 'react';

interface Answer {
  id?: number;
  text: string;
  isCorrect: boolean;
}

interface Question {
  id?: number;
  text: string;
  type: string;
  points: number;
  answers: Answer[];
}

interface QuizFormProps {
  quizId?: number;
  onSuccess?: () => void;
  onCancel?: () => void;
}

const QuizForm: React.FC<QuizFormProps> = ({ quizId, onSuccess, onCancel }) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<'info' | 'questions'>('info');
  const [savedQuizId, setSavedQuizId] = useState<number | undefined>(quizId);

  const [formData, setFormData] = useState({
    title: '',
    description: '',
    category: '',
    timeLimitMinutes: 30,
    maxAttempts: 3,
    isPublished: false,
    passingScore: 70,
    tags: [] as string[]
  });

  const [questions, setQuestions] = useState<Question[]>([]);
  const [tagInput, setTagInput] = useState('');

  useEffect(() => {
    if (quizId) {
      loadQuizAndQuestions();
    }
  }, [quizId]);

  const loadQuizAndQuestions = async () => {
    try {
      const quizRes = await fetch(`http://localhost:8080/api/quizzes/${quizId}`);
      const quiz = await quizRes.json();
      setFormData({
        title: quiz.title,
        description: quiz.description || '',
        category: quiz.category,
        timeLimitMinutes: quiz.timeLimitMinutes,
        maxAttempts: quiz.maxAttempts,
        isPublished: quiz.isPublished,
        passingScore: quiz.passingScore,
        tags: quiz.tags || []
      });

      const questionsRes = await fetch(`http://localhost:8080/api/questions/quiz/${quizId}`);
      const loadedQuestions = await questionsRes.json();
      if (loadedQuestions.length > 0) {
        setQuestions(loadedQuestions);
      }
    } catch (err) {
      setError('Ошибка загрузки');
    }
  };

  const addQuestion = () => {
    setQuestions([...questions, { text: '', type: 'SINGLE', points: 1, answers: [{ text: '', isCorrect: false }] }]);
  };

  const removeQuestion = (index: number) => {
    if (window.confirm('Удалить вопрос?')) {
      setQuestions(questions.filter((_, i) => i !== index));
    }
  };

  const updateQuestion = (index: number, field: keyof Question, value: any) => {
    const newQuestions = [...questions];
    newQuestions[index] = { ...newQuestions[index], [field]: value };
    setQuestions(newQuestions);
  };

  const addAnswer = (questionIndex: number) => {
    const newQuestions = [...questions];
    newQuestions[questionIndex].answers.push({ text: '', isCorrect: false });
    setQuestions(newQuestions);
  };

  const updateAnswer = (questionIndex: number, answerIndex: number, field: keyof Answer, value: string | boolean) => {
    const newQuestions = [...questions];
    newQuestions[questionIndex].answers[answerIndex] = {
      ...newQuestions[questionIndex].answers[answerIndex],
      [field]: value
    };

    if (field === 'isCorrect' && value === true && newQuestions[questionIndex].type === 'SINGLE') {
      newQuestions[questionIndex].answers.forEach((_, idx) => {
        if (idx !== answerIndex) {
          newQuestions[questionIndex].answers[idx].isCorrect = false;
        }
      });
    }

    setQuestions(newQuestions);
  };

  const removeAnswer = (questionIndex: number, answerIndex: number) => {
    const newQuestions = [...questions];
    newQuestions[questionIndex].answers = newQuestions[questionIndex].answers.filter((_, i) => i !== answerIndex);
    setQuestions(newQuestions);
  };

  const addTag = () => {
    if (tagInput.trim() && !formData.tags.includes(tagInput.trim())) {
      setFormData({ ...formData, tags: [...formData.tags, tagInput.trim()] });
      setTagInput('');
    }
  };

  const removeTag = (tag: string) => {
    setFormData({ ...formData, tags: formData.tags.filter(t => t !== tag) });
  };

  const saveQuiz = async () => {
    if (!formData.title.trim()) {
      throw new Error('Введите название квиза');
    }
    if (!formData.category.trim()) {
      throw new Error('Введите категорию');
    }

    const url = savedQuizId
      ? `http://localhost:8080/api/quizzes/${savedQuizId}`
      : 'http://localhost:8080/api/quizzes';
    const method = savedQuizId ? 'PUT' : 'POST';

    const res = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(formData)
    });

    if (!res.ok) {
      throw new Error('Ошибка сохранения квиза');
    }

    const savedQuiz = await res.json();
    return savedQuiz.id;
  };

  const saveQuestions = async (quizId: number) => {
    for (const question of questions) {
      if (!question.text.trim()) continue;

      const hasCorrect = question.answers.some(a => a.isCorrect);
      if (!hasCorrect) continue;

      const res = await fetch('http://localhost:8080/api/questions', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          text: question.text,
          type: question.type,
          points: question.points,
          quizId: quizId,
          answers: question.answers
        })
      });

      if (!res.ok) {
        throw new Error('Ошибка сохранения вопроса');
      }
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const quizId = await saveQuiz();
      setSavedQuizId(quizId);

      if (questions.length > 0) {
        await saveQuestions(quizId);
      }

      if (onSuccess) onSuccess();
    } catch (err: any) {
      setError(err.message || 'Ошибка сохранения');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: 900, margin: '0 auto', padding: 20 }}>
      <h1>{savedQuizId ? ' Редактировать квиз' : ' Создать квиз'}</h1>

      <div style={{ display: 'flex', gap: 10, marginBottom: 20, borderBottom: '1px solid #ddd' }}>
        <button
          onClick={() => setActiveTab('info')}
          style={{
            padding: '10px 20px',
            background: 'none',
            border: 'none',
            borderBottom: activeTab === 'info' ? '2px solid #007bff' : 'none',
            cursor: 'pointer',
            color: activeTab === 'info' ? '#007bff' : '#666'
          }}
        >
          Основная информация
        </button>
        <button
          onClick={() => setActiveTab('questions')}
          style={{
            padding: '10px 20px',
            background: 'none',
            border: 'none',
            borderBottom: activeTab === 'questions' ? '2px solid #007bff' : 'none',
            cursor: 'pointer',
            color: activeTab === 'questions' ? '#007bff' : '#666'
          }}
        >
          Вопросы ({questions.length})
        </button>
      </div>

      {error && <div style={{ color: 'red', padding: 10, backgroundColor: '#ffebee', borderRadius: 4, marginBottom: 20 }}>{error}</div>}

      <form onSubmit={handleSubmit}>
        {activeTab === 'info' && (
          <>
            <div style={{ marginBottom: 15 }}>
              <label style={{ display: 'block', marginBottom: 5, fontWeight: 'bold' }}>Название *</label>
              <input type="text" value={formData.title} onChange={e => setFormData({ ...formData, title: e.target.value })} required style={{ width: '100%', padding: 10, border: '1px solid #ddd', borderRadius: 4 }} />
            </div>

            <div style={{ marginBottom: 15 }}>
              <label style={{ display: 'block', marginBottom: 5, fontWeight: 'bold' }}>Описание</label>
              <textarea value={formData.description} onChange={e => setFormData({ ...formData, description: e.target.value })} rows={3} style={{ width: '100%', padding: 10, border: '1px solid #ddd', borderRadius: 4 }} />
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 15, marginBottom: 15 }}>
              <div>
                <label style={{ display: 'block', marginBottom: 5, fontWeight: 'bold' }}>Категория *</label>
                <input type="text" value={formData.category} onChange={e => setFormData({ ...formData, category: e.target.value })} required style={{ width: '100%', padding: 10, border: '1px solid #ddd', borderRadius: 4 }} />
              </div>
              <div>
                <label style={{ display: 'block', marginBottom: 5, fontWeight: 'bold' }}>Время (минуты)</label>
                <input type="number" value={formData.timeLimitMinutes} onChange={e => setFormData({ ...formData, timeLimitMinutes: parseInt(e.target.value) })} min={1} style={{ width: '100%', padding: 10, border: '1px solid #ddd', borderRadius: 4 }} />
              </div>
            </div>

            <div style={{ marginBottom: 15 }}>
              <label style={{ display: 'block', marginBottom: 5, fontWeight: 'bold' }}>Теги</label>
              <div style={{ display: 'flex', gap: 10, marginBottom: 10 }}>
                <input type="text" value={tagInput} onChange={e => setTagInput(e.target.value)} onKeyPress={e => e.key === 'Enter' && (e.preventDefault(), addTag())} placeholder="Добавить тег" style={{ flex: 1, padding: 10, border: '1px solid #ddd', borderRadius: 4 }} />
                <button type="button" onClick={addTag} style={{ padding: '10px 20px', backgroundColor: '#28a745', color: 'white', border: 'none', borderRadius: 4, cursor: 'pointer' }}>+</button>
              </div>
              <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
                {formData.tags.map(tag => (
                  <span key={tag} style={{ backgroundColor: '#e9ecef', padding: '4px 12px', borderRadius: 16, fontSize: 14, display: 'flex', alignItems: 'center', gap: 8 }}>
                    #{tag}
                    <button type="button" onClick={() => removeTag(tag)} style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#dc3545', fontSize: 16 }}>×</button>
                  </span>
                ))}
              </div>
            </div>

            <div style={{ marginBottom: 20 }}>
              <label style={{ display: 'flex', alignItems: 'center', gap: 10, cursor: 'pointer' }}>
                <input type="checkbox" checked={formData.isPublished} onChange={e => setFormData({ ...formData, isPublished: e.target.checked })} />
                <span>Опубликовать квиз</span>
              </label>
            </div>
          </>
        )}

        {activeTab === 'questions' && (
          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 15 }}>
              <h3>Вопросы ({questions.length})</h3>
              <button type="button" onClick={addQuestion} style={{ padding: '10px 20px', backgroundColor: '#28a745', color: 'white', border: 'none', borderRadius: 4, cursor: 'pointer' }}>
                + Добавить вопрос
              </button>
            </div>

            {questions.length === 0 && (
              <div style={{ textAlign: 'center', padding: 50, backgroundColor: '#f9f9f9', borderRadius: 8 }}>
                <p>Нет вопросов. Нажмите "+ Добавить вопрос"</p>
              </div>
            )}

            {questions.map((question, qIdx) => (
              <div key={qIdx} style={{ border: '1px solid #ddd', padding: 15, borderRadius: 8, marginBottom: 15, backgroundColor: '#fafafa' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 10 }}>
                  <strong>Вопрос {qIdx + 1}</strong>
                  <button type="button" onClick={() => removeQuestion(qIdx)} style={{ padding: '5px 10px', backgroundColor: '#dc3545', color: 'white', border: 'none', borderRadius: 4, cursor: 'pointer' }}>
                    Удалить
                  </button>
                </div>

                <input
                  type="text"
                  placeholder="Текст вопроса"
                  value={question.text}
                  onChange={e => updateQuestion(qIdx, 'text', e.target.value)}
                  style={{ width: '100%', padding: 10, border: '1px solid #ddd', borderRadius: 4, marginBottom: 10 }}
                />

                <div style={{ display: 'flex', gap: 15, marginBottom: 10 }}>
                  <select
                    value={question.type}
                    onChange={e => updateQuestion(qIdx, 'type', e.target.value)}
                    style={{ flex: 1, padding: 10, border: '1px solid #ddd', borderRadius: 4 }}
                  >
                    <option value="SINGLE">Одиночный выбор</option>
                    <option value="MULTIPLE">Множественный выбор</option>
                  </select>
                  <input
                    type="number"
                    placeholder="Баллы"
                    value={question.points}
                    onChange={e => updateQuestion(qIdx, 'points', parseInt(e.target.value))}
                    min={1}
                    style={{ width: 100, padding: 10, border: '1px solid #ddd', borderRadius: 4 }}
                  />
                </div>

                <div style={{ marginLeft: 20 }}>
                  <label style={{ fontWeight: 'bold' }}>Варианты ответов:</label>
                  {question.answers.map((answer, aIdx) => (
                    <div key={aIdx} style={{ display: 'flex', gap: 10, marginTop: 10, alignItems: 'center' }}>
                      <input
                        type="text"
                        placeholder="Текст ответа"
                        value={answer.text}
                        onChange={e => updateAnswer(qIdx, aIdx, 'text', e.target.value)}
                        style={{ flex: 1, padding: 8, border: '1px solid #ddd', borderRadius: 4 }}
                      />
                      <label style={{ display: 'flex', alignItems: 'center', gap: 5 }}>
                        <input
                          type="checkbox"
                          checked={answer.isCorrect}
                          onChange={e => updateAnswer(qIdx, aIdx, 'isCorrect', e.target.checked)}
                        />
                        Правильный
                      </label>
                      <button
                        type="button"
                        onClick={() => removeAnswer(qIdx, aIdx)}
                        style={{ padding: '5px 10px', backgroundColor: '#dc3545', color: 'white', border: 'none', borderRadius: 4, cursor: 'pointer' }}
                      >
                        ✕
                      </button>
                    </div>
                  ))}
                  <button
                    type="button"
                    onClick={() => addAnswer(qIdx)}
                    style={{ marginTop: 10, padding: '5px 15px', backgroundColor: '#6c757d', color: 'white', border: 'none', borderRadius: 4, cursor: 'pointer' }}
                  >
                    + Добавить ответ
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}

        <div style={{ display: 'flex', gap: 15, marginTop: 30 }}>
          <button type="submit" disabled={loading} style={{ padding: '12px 24px', backgroundColor: '#007bff', color: 'white', border: 'none', borderRadius: 4, cursor: 'pointer' }}>
            {loading ? 'Сохранение...' : (savedQuizId ? 'Обновить' : 'Создать')}
          </button>
          <button type="button" onClick={onCancel} style={{ padding: '12px 24px', backgroundColor: '#6c757d', color: 'white', border: 'none', borderRadius: 4, cursor: 'pointer' }}>
            Отмена
          </button>
        </div>
      </form>
    </div>
  );
};

export default QuizForm;