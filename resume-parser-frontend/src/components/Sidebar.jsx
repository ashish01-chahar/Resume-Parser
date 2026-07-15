import React from 'react';
import { Database, UploadCloud, BarChart2, Settings } from 'lucide-react';

export default function Sidebar({ currentView, setView }) {
  const navItems = [
    { id: 'dashboard', label: 'Talent Directory', icon: <Database size={20} /> },
    { id: 'upload', label: 'Parse Resume', icon: <UploadCloud size={20} /> },
    { id: 'analytics', label: 'Analytics', icon: <BarChart2 size={20} /> },
    { id: 'settings', label: 'Settings', icon: <Settings size={20} /> },
  ];

  return (
    <aside style={{
      width: '260px',
      background: 'rgba(30, 41, 59, 0.1)',
      borderRight: '1px solid var(--border-color)',
      padding: '2rem 1rem',
      display: 'flex',
      flexDirection: 'column',
      gap: '0.5rem',
      minHeight: 'calc(100vh - 72px)'
    }}>
      <div style={{ padding: '0 1rem', marginBottom: '1rem', fontSize: '0.8rem', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
        Main Menu
      </div>
      {navItems.map(item => (
        <button
          key={item.id}
          onClick={() => setView(item.id)}
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '0.75rem',
            padding: '0.85rem 1rem',
            borderRadius: 'var(--radius-md)',
            background: currentView === item.id ? 'var(--primary)' : 'transparent',
            color: currentView === item.id ? '#fff' : 'var(--text-secondary)',
            border: 'none',
            cursor: 'pointer',
            textAlign: 'left',
            fontWeight: 500,
            transition: 'all 0.2s ease'
          }}
          onMouseEnter={(e) => {
            if (currentView !== item.id) {
              e.currentTarget.style.background = 'var(--bg-secondary)';
              e.currentTarget.style.color = 'var(--text-primary)';
            }
          }}
          onMouseLeave={(e) => {
            if (currentView !== item.id) {
              e.currentTarget.style.background = 'transparent';
              e.currentTarget.style.color = 'var(--text-secondary)';
            }
          }}
        >
          {item.icon}
          {item.label}
        </button>
      ))}
    </aside>
  );
}
