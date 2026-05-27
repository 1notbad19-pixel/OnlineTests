import React from 'react';
import { useTheme } from '../context/ThemeContext';

interface ConfirmModalProps {
  isOpen: boolean;
  title: string;
  message: string;
  onConfirm: () => void;
  onCancel: () => void;
}

const ConfirmModal: React.FC<ConfirmModalProps> = ({ isOpen, title, message, onConfirm, onCancel }) => {
  const { theme } = useTheme();

  if (!isOpen) return null;

  return (
    <div style={{
      position: 'fixed',
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      backgroundColor: 'rgba(0, 0, 0, 0.6)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      zIndex: 1000,
      backdropFilter: 'blur(4px)'
    }}>
      <div className="google-card" style={{
        maxWidth: 450,
        width: '90%',
        padding: 28,
        textAlign: 'center',
        animation: 'fadeIn 0.2s ease',
        backgroundColor: 'var(--bg-card)',
        borderRadius: 16,
        border: '1px solid var(--border-color)'
      }}>
        <div style={{ fontSize: 48, marginBottom: 16 }}></div>
        <h3 style={{ marginBottom: 12, fontSize: 22, fontWeight: 500, color: 'var(--text-primary)' }}>{title}</h3>
        <p style={{ color: 'var(--text-secondary)', marginBottom: 24, fontSize: 15 }}>{message}</p>
        <div style={{ display: 'flex', gap: 12, justifyContent: 'center' }}>
          <button
            onClick={onCancel}
            className="google-btn-secondary"
            style={{ padding: '10px 24px', fontSize: 14 }}
          >
            Отмена
          </button>
          <button
            onClick={onConfirm}
            className="google-btn-danger"
            style={{ padding: '10px 24px', fontSize: 14 }}
          >
            Удалить
          </button>
        </div>
      </div>
    </div>
  );
};

export default ConfirmModal;