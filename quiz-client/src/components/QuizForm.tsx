import React, { useState, useEffect } from 'react';
import { quizApi } from '../services/api';
import { QuizRequest } from '../types';

interface QuizFormProps {
  quizId?: number;
  onSuccess?: () => void;
  onCancel?: () => void;
}

const QuizForm: React.FC<QuizFormProps> = ({ quizId, onSuccess, onCancel }) => {
  const [formData, setFormData] = useState<QuizRequest>({
    title: '',
    description: '',
    category: '',
    timeLimitMinutes: 30,
    maxAttempts: 3,
    isPublished: false,
    passingScore: 70,
    tags: [],
  });
  const [tagInput, setTagInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (quizId) {
      loadQuiz();
    }
  }, [quizId]);

  const loadQuiz = async () => {
    try {
      const response = await quizApi.getById(quizId!);
      const quiz = response.data;
      setFormData({
        title: quiz.title,
        description: quiz.description,
        category: quiz.category,
        timeLimitMinutes: quiz.timeLimitMinutes,
        maxAttempts: quiz.maxAttempts,
        isPublished: quiz.isPublished,
        passingScore: quiz.passingScore,
        tags: quiz.tags,
      });
    } catch (err) {
      setError('Ошибка загрузки квиза');
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value, type } = e.target as any;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? (e.target as any).checked : value,
    }));
  };

  const addTag = () => {
    if (tagInput.trim() && !formData.tags?.includes(tagInput.trim())) {
      setFormData(prev => ({
        ...prev,
        tags: [...(prev.tags || []), tagInput.trim()],
      }));
      setTagInput('');
    }
  };

  const removeTag = (tag: string) => {
    setFormData(prev => ({
      ...prev,
      tags: prev.tags?.filter(t => t !== tag),
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      if (quizId) {
        await quizApi.update(quizId, formData);
      } else {
        await quizApi.create(formData);
      }
      onSuccess?.();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Ошибка сохранения');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: '800px', margin: '0 auto', padding: '20px' }}>
      <h1>{quizId ? '✏️ Редактировать квиз' : '➕ Создать квиз'}</h1>

      {error && <div style={{ color: 'red', padding: '10px', backgroundColor: '#ffebee', borderRadius: '4px', marginBottom: '20px' }}>{error}</div>}

      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: '15px' }}>
          <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>Название *</label>
          <input
            type="text"
            name="title"
            value={formData.title}
            onChange={handleChange}
            required
            style={{ width: '100%', padding: '10px', border: '1px solid #ddd', borderRadius: '4px' }}
          />
        </div>

        <div style={{ marginBottom: '15px' }}>
          <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>Описание</label>
          <textarea
            name="description"
            value={formData.description}
            onChange={handleChange}
            rows={3}
            style={{ width: '100%', padding: '10px', border: '1px solid #ddd', borderRadius: '4px' }}
          />
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px', marginBottom: '15px' }}>
          <div>
            <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>Категория *</label>
            <input
              type="text"
              name="category"
              value={formData.category}
              onChange={handleChange}
              required
              style={{ width: '100%', padding: '10px', border: '1px solid #ddd', borderRadius: '4px' }}
            />
          </div>

          <div>
            <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>Время (минуты)</label>
            <input
              type="number"
              name="timeLimitMinutes"
              value={formData.timeLimitMinutes}
              onChange={handleChange}
              min={1}
              style={{ width: '100%', padding: '10px', border: '1px solid #ddd', borderRadius: '4px' }}
            />
          </div>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px', marginBottom: '15px' }}>
          <div>
            <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>Макс. попыток</label>
            <input
              type="number"
              name="maxAttempts"
              value={formData.maxAttempts}
              onChange={handleChange}
              min={1}
              style={{ width: '100%', padding: '10px', border: '1px solid #ddd', borderRadius: '4px' }}
            />
          </div>

          <div>
            <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>Проходной балл (%)</label>
            <input
              type="number"
              name="passingScore"
              value={formData.passingScore}
              onChange={handleChange}
              min={0}
              max={100}
              style={{ width: '100%', padding: '10px', border: '1px solid #ddd', borderRadius: '4px' }}
            />
          </div>
        </div>

        <div style={{ marginBottom: '15px' }}>
          <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>Теги</label>
          <div style={{ display: 'flex', gap: '10px', marginBottom: '10px' }}>
            <input
              type="text"
              value={tagInput}
              onChange={(e) => setTagInput(e.target.value)}
              onKeyPress={(e) => e.key === 'Enter' && (e.preventDefault(), addTag())}
              placeholder="Добавить тег"
              style={{ flex: 1, padding: '10px', border: '1px solid #ddd', borderRadius: '4px' }}
            />
            <button type="button" onClick={addTag} style={{ padding: '10px 20px', backgroundColor: '#28a745', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>+</button>
          </div>
          <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
            {formData.tags?.map((tag) => (
              <span key={tag} style={{ backgroundColor: '#e9ecef', padding: '4px 12px', borderRadius: '16px', fontSize: '14px', display: 'flex', alignItems: 'center', gap: '8px' }}>
                #{tag}
                <button type="button" onClick={() => removeTag(tag)} style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#dc3545', fontSize: '16px' }}>×</button>
              </span>
            ))}
          </div>
        </div>

        <div style={{ marginBottom: '20px' }}>
          <label style={{ display: 'flex', alignItems: 'center', gap: '10px', cursor: 'pointer' }}>
            <input
              type="checkbox"
              name="isPublished"
              checked={formData.isPublished}
              onChange={handleChange}
            />
            <span>Опубликовать квиз</span>
          </label>
        </div>

        <div style={{ display: 'flex', gap: '15px' }}>
          <button type="submit" disabled={loading} style={{ padding: '12px 24px', backgroundColor: '#007bff', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
            {loading ? 'Сохранение...' : (quizId ? 'Обновить' : 'Создать')}
          </button>
          <button type="button" onClick={onCancel} style={{ padding: '12px 24px', backgroundColor: '#6c757d', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
            Отмена
          </button>
        </div>
      </form>
    </div>
  );
};

export default QuizForm;