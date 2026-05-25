import React, { useState } from 'react';
import QuizList from './components/QuizList';
import QuizForm from './components/QuizForm';
import QuizDetails from './components/QuizDetails';

type View = 'list' | 'create' | 'edit' | 'details';

const App: React.FC = () => {
  const [currentView, setCurrentView] = useState<View>('list');
  const [selectedQuizId, setSelectedQuizId] = useState<number | undefined>();

  const handleEdit = (id: number) => {
    setSelectedQuizId(id);
    setCurrentView('edit');
  };

  const handleView = (id: number) => {
    setSelectedQuizId(id);
    setCurrentView('details');
  };

  const handleBack = () => {
    setCurrentView('list');
    setSelectedQuizId(undefined);
  };

  const handleSuccess = () => {
    setCurrentView('list');
    setSelectedQuizId(undefined);
  };

  return (
    <div>
      <nav style={{ backgroundColor: '#343a40', padding: '15px 20px', color: 'white' }}>
        <div style={{ maxWidth: '1200px', margin: '0 auto', display: 'flex', gap: '30px', alignItems: 'center' }}>
          <h2 style={{ margin: 0, cursor: 'pointer' }} onClick={() => handleBack()}>
            📚 Online Test
          </h2>
          <button
            onClick={() => setCurrentView('create')}
            style={{
              background: 'none',
              border: '1px solid white',
              color: 'white',
              padding: '5px 15px',
              borderRadius: '4px',
              cursor: 'pointer'
            }}
          >
            ➕ Создать квиз
          </button>
        </div>
      </nav>

      {currentView === 'list' && <QuizList onEdit={handleEdit} onView={handleView} />}

      {currentView === 'create' && <QuizForm onSuccess={handleSuccess} onCancel={handleBack} />}

      {currentView === 'edit' && selectedQuizId && (
        <QuizForm quizId={selectedQuizId} onSuccess={handleSuccess} onCancel={handleBack} />
      )}

      {currentView === 'details' && selectedQuizId && (
        <QuizDetails quizId={selectedQuizId} onBack={handleBack} onEdit={() => handleEdit(selectedQuizId)} />
      )}
    </div>
  );
};

export default App;