import React from 'react';
import { Sparkles, Bell, User } from 'lucide-react';

export default function Navbar() {
  return (
    <header className="navbar">
      <div className="logo-container">
        <Sparkles className="logo-icon" />
        <span>ResumeIntellect</span>
      </div>
      
      <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
        <button className="icon-btn" style={{ background: 'transparent', border: 'none', color: 'var(--text-secondary)', cursor: 'pointer' }}>
          <Bell size={20} />
        </button>
        <div style={{ 
          display: 'flex', 
          alignItems: 'center', 
          gap: '0.75rem', 
          padding: '0.35rem 0.75rem', 
          background: 'var(--bg-secondary)', 
          borderRadius: '9999px',
          border: '1px solid var(--border-color)',
          cursor: 'pointer'
        }}>
          <div style={{ width: '28px', height: '28px', borderRadius: '50%', background: 'var(--primary)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#fff' }}>
            <User size={16} />
          </div>
          <span style={{ fontSize: '0.9rem', fontWeight: 500, color: 'var(--text-primary)' }}>Admin</span>
        </div>
      </div>
    </header>
  );
}
