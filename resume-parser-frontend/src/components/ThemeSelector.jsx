import React from 'react';
import { motion } from 'framer-motion';
import { Check } from 'lucide-react';

const themes = [
  { id: 'theme-midnight', name: 'Midnight Blue', bg: '#09090F', primary: '#7C5CFF', card: '#1A2033' },
  { id: 'theme-emerald-light', name: 'Emerald Light', bg: '#FFFFFF', primary: '#10B981', card: '#F8FAFC' },
  { id: 'theme-monochrome', name: 'Monochrome', bg: '#FFFFFF', primary: '#000000', card: '#F3F4F6' },
  { id: 'theme-ocean-dark', name: 'Ocean Dark', bg: '#07131F', primary: '#2563EB', card: '#10233C' }
];

export default function ThemeSelector({ currentTheme, onSelectTheme }) {
  return (
    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1.5rem', width: '100%' }}>
      {themes.map((theme) => (
        <motion.div
          key={theme.id}
          whileHover={{ y: -4 }}
          onClick={() => onSelectTheme(theme.id)}
          style={{
            cursor: 'pointer',
            border: currentTheme === theme.id ? `2px solid ${theme.primary}` : '2px solid transparent',
            borderRadius: '16px',
            overflow: 'hidden',
            boxShadow: currentTheme === theme.id ? `0 0 20px ${theme.primary}40` : '0 4px 6px rgba(0,0,0,0.1)'
          }}
        >
          {/* Theme Preview Card */}
          <div style={{ background: theme.bg, height: '120px', padding: '1rem', display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
            {/* Mock Navbar */}
            <div style={{ height: '12px', width: '100%', background: theme.card, borderRadius: '4px' }}></div>
            {/* Mock Layout */}
            <div style={{ display: 'flex', gap: '0.5rem', flex: 1 }}>
              <div style={{ width: '30%', height: '100%', background: theme.card, borderRadius: '4px' }}></div>
              <div style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                <div style={{ height: '50%', background: theme.card, borderRadius: '4px', display: 'flex', alignItems: 'center', padding: '0 0.5rem' }}>
                    <div style={{ height: '8px', width: '40%', background: theme.primary, borderRadius: '2px' }}></div>
                </div>
                <div style={{ height: '50%', background: theme.card, borderRadius: '4px' }}></div>
              </div>
            </div>
          </div>
          
          <div style={{ 
            background: 'var(--card-bg)', 
            padding: '1rem', 
            display: 'flex', 
            justifyContent: 'space-between', 
            alignItems: 'center',
            borderTop: '1px solid var(--border-color)'
          }}>
            <span style={{ fontWeight: 600, color: 'var(--text-primary)' }}>{theme.name}</span>
            {currentTheme === theme.id && <Check size={18} color={theme.primary} />}
          </div>
        </motion.div>
      ))}
    </div>
  );
}
