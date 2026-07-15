import React, { useState, useEffect } from 'react';
import Navbar from './components/Navbar';
import Sidebar from './components/Sidebar';
import Dashboard from './components/Dashboard';
import Upload from './components/Upload';
import CandidateDetails from './components/CandidateDetails';
import Analytics from './components/Analytics';
import Settings from './components/Settings';
import Onboarding from './components/Onboarding';
import { Sparkles, X } from 'lucide-react';

function App() {
  const [hasOnboarded, setHasOnboarded] = useState(
    localStorage.getItem('resume-parser-onboarded') === 'true'
  );
  
  const [view, setView] = useState('dashboard');
  const [selectedCandidateId, setSelectedCandidateId] = useState(null);
  
  const [notification, setNotification] = useState({ show: false, type: '', message: '' });

  useEffect(() => {
    // Load theme on startup
    const theme = localStorage.getItem('resume-parser-theme') || 'theme-midnight';
    document.body.className = theme;
  }, []);

  const triggerNotification = (type, message) => {
    setNotification({ show: true, type, message });
    setTimeout(() => setNotification({ show: false, type: '', message: '' }), 5000);
  };

  const handleOnboardComplete = (theme) => {
    document.body.className = theme;
    setHasOnboarded(true);
  };

  if (!hasOnboarded) {
    return <Onboarding onComplete={handleOnboardComplete} />;
  }

  return (
    <div className="app-container">
      <Navbar />
      
      <div style={{ display: 'flex', flex: 1, position: 'relative' }}>
        <Sidebar currentView={view} setView={setView} />
        
        <main style={{ flex: 1, padding: '2rem', height: 'calc(100vh - 72px)', overflowY: 'auto', position: 'relative' }}>
          
          {/* Global Notification Toast */}
          {notification.show && (
            <div style={{
              position: 'fixed',
              top: '90px',
              right: '2rem',
              zIndex: 1000,
              display: 'flex',
              alignItems: 'center',
              gap: '0.75rem',
              padding: '1rem 1.5rem',
              background: 'var(--card-bg)',
              border: `1px solid ${notification.type === 'error' ? 'var(--danger)' : 'var(--success)'}`,
              borderRadius: '12px',
              boxShadow: 'var(--shadow-lg)',
              color: 'var(--text-primary)',
              animation: 'fade-in 0.3s ease'
            }}>
              {notification.type === 'error' ? <X size={20} color="var(--danger)" /> : <Sparkles size={20} color="var(--success)" />}
              <span style={{ fontWeight: 500 }}>{notification.message}</span>
            </div>
          )}

          {/* View Router */}
          {view === 'dashboard' && <Dashboard setView={setView} setSelectedCandidateId={setSelectedCandidateId} triggerNotification={triggerNotification} />}
          {view === 'upload' && <Upload setView={setView} setSelectedCandidateId={setSelectedCandidateId} triggerNotification={triggerNotification} />}
          {view === 'details' && <CandidateDetails candidateId={selectedCandidateId} setView={setView} triggerNotification={triggerNotification} />}
          {view === 'analytics' && <Analytics triggerNotification={triggerNotification} />}
          {view === 'settings' && <Settings triggerNotification={triggerNotification} />}
          
        </main>
      </div>
    </div>
  );
}

export default App;
