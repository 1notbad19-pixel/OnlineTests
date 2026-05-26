import React, { useEffect, useState } from 'react';
import { useTheme } from '../context/ThemeContext';

interface QuizResult {
  id: number;
  quizId: number;
  quizTitle: string;
  score: number;
  percentage: number;
  completedAt: string;
}

interface MyResultsProps {
  currentUserId?: number;
}

const MyResults: React.FC<MyResultsProps> = ({ currentUserId }) => {
  const { theme } = useTheme();
  const [results, setResults] = useState<QuizResult[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setTimeout(() => {
      const demoResults: QuizResult[] = [
        { id: 1, quizId: 1, quizTitle: 'Java программирование', score: 85, percentage: 85, completedAt: new Date().toISOString() },
        { id: 2, quizId: 2, quizTitle: 'Spring Boot', score: 70, percentage: 70, completedAt: new Date().toISOString() },
        { id: 3, quizId: 3, quizTitle: 'Базы данных SQL', score: 95, percentage: 95, completedAt: new Date().toISOString() }
      ];
      setResults(demoResults);
      setLoading(false);
    }, 500);
  }, []);

  if (loading) return <div style={{ padding: 50, textAlign: 'center' }}>Загрузка результатов...</div>;
  if (error) return <div style={{ padding: 50, textAlign: 'center', color: 'var(--error-color)' }}>Ошибка: {error}</div>;

  const averagePercentage = results.length > 0
    ? Math.round(results.reduce((sum, r) => sum + r.percentage, 0) / results.length)
    : 0;

  return (
    <div style={{ maxWidth: 1000, margin: '0 auto', padding: 20 }}>
      <h1 style={{ fontSize: 28, fontWeight: 500, marginBottom: 8 }}> Мои результаты</h1>

      <div style={{ backgroundColor: 'var(--bg-card)', padding: 20, borderRadius: 8, marginBottom: 30, textAlign: 'center', border: '1px solid var(--border-color)' }}>
        <div style={{ fontSize: 48, fontWeight: 'bold', color: 'var(--success-color)' }}>{averagePercentage}%</div>
        <div style={{ color: 'var(--text-secondary)' }}>Средний балл</div>
        <div style={{ color: 'var(--text-secondary)' }}>Пройдено тестов: {results.length}</div>
      </div>

      {results.length === 0 && <p>Вы еще не проходили ни одного теста</p>}

      {results.map((result) => (
        <div key={result.id} style={{ backgroundColor: 'var(--bg-card)', border: '1px solid var(--border-color)', borderRadius: 8, padding: 15, marginBottom: 15 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap' }}>
            <div>
              <h3 style={{ color: 'var(--text-primary)' }}>{result.quizTitle}</h3>
              <div style={{ color: 'var(--text-secondary)', fontSize: 14 }}>Пройден: {new Date(result.completedAt).toLocaleDateString()}</div>
            </div>
            <div style={{ textAlign: 'center' }}>
              <div style={{ fontSize: 32, fontWeight: 'bold', color: result.percentage >= 70 ? 'var(--success-color)' : 'var(--error-color)' }}>
                {result.percentage}%
              </div>
              <div style={{ fontSize: 12, color: 'var(--text-secondary)' }}>баллов</div>
            </div>
          </div>
          <div style={{ marginTop: 10 }}>
            <div style={{ width: '100%', height: 8, backgroundColor: 'var(--bg-hover)', borderRadius: 4 }}>
              <div style={{ width: `${result.percentage}%`, height: 8, backgroundColor: result.percentage >= 70 ? 'var(--success-color)' : 'var(--error-color)', borderRadius: 4 }} />
            </div>
          </div>
        </div>
      ))}
    </div>
  );
};

export default MyResults;