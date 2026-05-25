import React from 'react';

interface FilterBarProps {
  category: string;
  setCategory: (value: string) => void;
  published: string;
  setPublished: (value: string) => void;
  minQuestions: string;
  setMinQuestions: (value: string) => void;
  onApply: () => void;
  onReset: () => void;
}

const FilterBar: React.FC<FilterBarProps> = ({
  category,
  setCategory,
  published,
  setPublished,
  minQuestions,
  setMinQuestions,
  onApply,
  onReset,
}) => {
  return (
    <div style={{
      display: 'flex',
      gap: '15px',
      padding: '20px',
      backgroundColor: '#f5f5f5',
      borderRadius: '8px',
      marginBottom: '20px',
      flexWrap: 'wrap'
    }}>
      <input
        type="text"
        placeholder="Категория"
        value={category}
        onChange={(e) => setCategory(e.target.value)}
        style={{ padding: '8px 12px', borderRadius: '4px', border: '1px solid #ddd' }}
      />

      <select
        value={published}
        onChange={(e) => setPublished(e.target.value)}
        style={{ padding: '8px 12px', borderRadius: '4px', border: '1px solid #ddd' }}
      >
        <option value="">Все статусы</option>
        <option value="true">Опубликован</option>
        <option value="false">Черновик</option>
      </select>

      <input
        type="number"
        placeholder="Мин. вопросов"
        value={minQuestions}
        onChange={(e) => setMinQuestions(e.target.value)}
        style={{ padding: '8px 12px', borderRadius: '4px', border: '1px solid #ddd', width: '120px' }}
      />

      <button onClick={onApply} style={{ padding: '8px 20px', backgroundColor: '#007bff', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
        Применить
      </button>

      <button onClick={onReset} style={{ padding: '8px 20px', backgroundColor: '#6c757d', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
        Сбросить
      </button>
    </div>
  );
};

export default FilterBar;