import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { Sparkles, ArrowRight } from 'lucide-react';
import ThemeSelector from './ThemeSelector';

export default function Onboarding({ onComplete }) {
  const [step, setStep] = useState(1);
  const [selectedTheme, setSelectedTheme] = useState('theme-midnight');

  const handleNext = () => {
    if (step === 1) setStep(2);
    else {
      // Save theme to localStorage and complete
      localStorage.setItem('resume-parser-theme', selectedTheme);
      localStorage.setItem('resume-parser-onboarded', 'true');
      onComplete(selectedTheme);
    }
  };

  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '2rem',
      background: 'var(--bg-primary)'
    }}>
      <motion.div 
        className="glass-panel"
        initial={{ opacity: 0, scale: 0.9 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ duration: 0.5 }}
        style={{ maxWidth: '800px', width: '100%', position: 'relative', overflow: 'hidden' }}
      >
        {/* Animated background glow */}
        <div style={{
          position: 'absolute',
          top: '-50%', left: '-50%',
          width: '200%', height: '200%',
          background: 'radial-gradient(circle, var(--primary) 0%, transparent 60%)',
          opacity: 0.05,
          zIndex: 0,
          pointerEvents: 'none'
        }} />

        <div style={{ position: 'relative', zIndex: 1 }}>
          {step === 1 ? (
            <motion.div 
              initial={{ x: 20, opacity: 0 }}
              animate={{ x: 0, opacity: 1 }}
              exit={{ x: -20, opacity: 0 }}
              style={{ textAlign: 'center', padding: '3rem 1rem' }}
            >
              <div style={{ display: 'inline-flex', padding: '1rem', background: 'var(--bg-secondary)', borderRadius: '24px', marginBottom: '2rem' }}>
                <Sparkles size={48} color="var(--primary)" />
              </div>
              <h1 style={{ fontSize: '3rem', marginBottom: '1rem' }}>Welcome to ResumeIntellect</h1>
              <p style={{ fontSize: '1.1rem', maxWidth: '500px', margin: '0 auto 3rem', color: 'var(--text-secondary)' }}>
                The premium enterprise talent core. Parse, manage, and analyze candidates seamlessly with Gemini AI extraction.
              </p>
              <button className="btn btn-primary" onClick={handleNext} style={{ padding: '1rem 2.5rem', fontSize: '1.1rem' }}>
                Get Started <ArrowRight size={20} />
              </button>
            </motion.div>
          ) : (
            <motion.div
              initial={{ x: 20, opacity: 0 }}
              animate={{ x: 0, opacity: 1 }}
              style={{ padding: '1rem' }}
            >
              <h2 style={{ fontSize: '2rem', marginBottom: '0.5rem' }}>Choose your workspace theme</h2>
              <p style={{ marginBottom: '2rem', color: 'var(--text-secondary)' }}>
                Select a visual style. You can always change this later in settings.
              </p>
              
              <ThemeSelector 
                currentTheme={selectedTheme} 
                onSelectTheme={(id) => {
                  setSelectedTheme(id);
                  document.body.className = id;
                }} 
              />
              
              <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '3rem' }}>
                <button className="btn btn-primary" onClick={handleNext} style={{ padding: '0.75rem 2rem' }}>
                  Launch Application <ArrowRight size={18} />
                </button>
              </div>
            </motion.div>
          )}
        </div>
      </motion.div>
    </div>
  );
}
