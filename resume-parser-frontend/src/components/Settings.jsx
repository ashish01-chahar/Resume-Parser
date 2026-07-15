import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Settings as SettingsIcon, Database, Key, Monitor } from 'lucide-react';
import ThemeSelector from './ThemeSelector';

export default function Settings({ triggerNotification }) {
  const [stats, setStats] = useState(null);
  const [currentTheme, setCurrentTheme] = useState(localStorage.getItem('resume-parser-theme') || 'theme-midnight');

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const res = await fetch('http://localhost:8080/api/dashboard/stats');
        if (res.ok) setStats(await res.json());
      } catch (err) {
        // Ignored
      }
    };
    fetchStats();
  }, []);

  const changeTheme = (id) => {
    setCurrentTheme(id);
    document.body.className = id;
    localStorage.setItem('resume-parser-theme', id);
    triggerNotification('success', 'Theme updated');
  };

  return (
    <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} style={{ padding: '2rem', maxWidth: '1000px', margin: '0 auto' }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '3rem' }}>
        <div style={{ padding: '1rem', background: 'var(--bg-secondary)', borderRadius: '12px', border: '1px solid var(--border-color)' }}>
          <SettingsIcon size={28} color="var(--primary)" />
        </div>
        <div>
          <h1 style={{ fontSize: '2rem', marginBottom: '0.25rem' }}>Application Settings</h1>
          <p>Configure preferences and view system status.</p>
        </div>
      </div>

      <div style={{ display: 'grid', gap: '2rem' }}>
        
        {/* Theme Settings */}
        <div className="glass-panel">
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1.5rem' }}>
            <Monitor color="var(--primary)" />
            <h2 style={{ fontSize: '1.25rem' }}>Appearance & Themes</h2>
          </div>
          <ThemeSelector currentTheme={currentTheme} onSelectTheme={changeTheme} />
        </div>

        {/* System Status */}
        <div className="glass-panel">
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1.5rem' }}>
            <Database color="var(--primary)" />
            <h2 style={{ fontSize: '1.25rem' }}>System Connections</h2>
          </div>
          
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '1rem' }}>
            <div style={{ padding: '1.5rem', background: 'var(--bg-primary)', borderRadius: '12px', border: '1px solid var(--border-color)' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1rem' }}>
                <Key size={20} color="var(--text-secondary)" />
                <h3 style={{ fontSize: '1.1rem' }}>Gemini API Integration</h3>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span style={{ color: 'var(--text-secondary)' }}>Status</span>
                <span className="tag" style={{ background: stats?.geminiStatus === 'Connected' ? 'rgba(16, 185, 129, 0.1)' : 'rgba(245, 158, 11, 0.1)', color: stats?.geminiStatus === 'Connected' ? 'var(--success)' : 'var(--warning)' }}>
                  {stats?.geminiStatus || 'Checking...'}
                </span>
              </div>
            </div>

            <div style={{ padding: '1.5rem', background: 'var(--bg-primary)', borderRadius: '12px', border: '1px solid var(--border-color)' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1rem' }}>
                <Database size={20} color="var(--text-secondary)" />
                <h3 style={{ fontSize: '1.1rem' }}>Database Node</h3>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span style={{ color: 'var(--text-secondary)' }}>Connection</span>
                <span className="tag" style={{ background: 'rgba(124, 92, 255, 0.1)', color: 'var(--primary)' }}>
                  {stats?.databaseStatus || 'Checking...'}
                </span>
              </div>
            </div>
            
            <div style={{ padding: '1.5rem', background: 'var(--bg-primary)', borderRadius: '12px', border: '1px solid var(--border-color)' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1rem' }}>
                <Monitor size={20} color="var(--text-secondary)" />
                <h3 style={{ fontSize: '1.1rem' }}>App Version</h3>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span style={{ color: 'var(--text-secondary)' }}>Release</span>
                <span className="tag">
                  {stats?.appVersion || 'v1.0.0'}
                </span>
              </div>
            </div>
          </div>
        </div>

      </div>
    </motion.div>
  );
}
