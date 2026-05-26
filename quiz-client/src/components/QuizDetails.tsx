import React, { useEffect, useState } from 'react';
import { quizApi, questionApi } from '../services/api';
import { Quiz, Question } from '../types';

interface QuizDetailsProps {
  quizId: number;
  onBack: () => void;
  onEdit: () => void;
}

const QuizDetails: React.FC<QuizDetailsProps> = ({ quizId, onBack, onEdit }) => {
  const [quiz, setQuiz] = useState<Quiz | null>(null);
  const [questions, setQuestions] = useState<Question[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadData();
  }, [quizId]);

  const loadData = async () => {
    try {
      const [quizRes, questionsRes] = await Promise.all([
        quizApi.getWithDetails(quizId),
        questionApi.getByQuizId(quizId)
      ]);
      setQuiz(quizRes.data);
      setQuestions(questionsRes.data);
      setError(null);
    } catch (err) {
      setError('Ошибка загрузки данных');
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div style={{ textAlign: 'center', padding: '50px' }}>Загрузка...</div>;
  if (error) return <div style={{ color: 'red', padding: '20px' }}>{error}</div>;
  if (!quiz) return <div>Квиз не найден</div>;

  return (
    <div style={{ maxWidth: '1000px', margin: '0 auto', padding: '20px' }}>
      <button onClick={onBack} style={{ marginBottom: '20px', padding: '8px 16px', cursor: 'pointer' }}>← Назад к списку</button>

      <div style={{ backgroundColor: 'white', borderRadius: '8px', padding: '30px', boxShadow: '0 2px 4px rgba(0,0,0,0.1)' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start' }}>
          <h1>{quiz.title}</h1>
          <button onClick={onEdit} style={{ padding: '8px 16px', backgroundColor: '#ffc107', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>✏️ Редактировать</button>
        </div>

        <p style={{ color: '#666', margin: '15px 0' }}>{quiz.description}</p>

        <hr style={{ margin: '20px 0' }} />

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: '15px', marginBottom: '30px' }}>
          <div><strong>Категория:</strong> {quiz.category}</div>
          <div><strong>Время:</strong> {quiz.timeLimitMinutes} минут</div>
          <div><strong>Макс. попыток:</strong> {quiz.maxAttempts || '∞'}</div>
          <div><strong>Проходной балл:</strong> {quiz.passingScore || 70}%</div>
          <div><strong>Статус:</strong> {quiz.isPublished ? '✅ Опубликован' : '📝 Черновик'}</div>
          <div><strong>Вопросов:</strong> {quiz.questionCount}</div>
        </div>

        <div style={{ marginBottom: '30px' }}>
          <strong>Теги:</strong>
          <div style={{ marginTop: '8px' }}>
            {quiz.tags?.map(tag => (
              <span key={tag} style={{ backgroundColor: '#e9ecef', padding: '4px 12px', borderRadius: '16px', fontSize: '14px', marginRight: '8px' }}>#{tag}</span>
            ))}
          </div>
        </div>

        <h2>Вопросы ({questions.length})</h2>
        {questions.map((q, idx) => (
          <div key={q.id} style={{ border: '1px solid #eee', borderRadius: '8px', padding: '15px', marginTop: '15px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <strong>Вопрос {idx + 1}: {q.text}</strong>
              <span style={{ color: '#888' }}>{q.points} баллов</span>
            </div>
            <div style={{ marginTop: '10px', marginLeft: '20px' }}>
              {q.answers?.map(a => (
                <div key={a.id} style={{ margin: '5px 0' }}>
                  {a.isCorrect ? '✅' : '○'} {a.text}
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default QuizDetails;