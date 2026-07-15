import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { UploadCloud, FileText, CheckCircle } from 'lucide-react';

export default function Upload({ setView, setSelectedCandidateId, triggerNotification }) {
  const [dragActive, setDragActive] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [progress, setProgress] = useState(0);

  const handleDrag = (e) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.type === "dragenter" || e.type === "dragover") setDragActive(true);
    else if (e.type === "dragleave") setDragActive(false);
  };

  const handleDrop = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      uploadFile(e.dataTransfer.files[0]);
    }
  };

  const uploadFile = async (file) => {
    setUploading(true);
    setProgress(10);
    
    // Simulate initial parsing
    const interval = setInterval(() => {
      setProgress(p => (p < 80 ? p + 10 : p));
    }, 500);

    const formData = new FormData();
    formData.append('file', file);
    
    try {
      const res = await fetch('http://localhost:8080/api/resumes/upload', {
        method: 'POST',
        body: formData,
      });
      clearInterval(interval);
      setProgress(100);
      
      if (res.ok) {
        const data = await res.json();
        triggerNotification('success', 'Resume parsed & stored successfully!');
        setTimeout(() => {
          setSelectedCandidateId(data.id);
          setView('details');
        }, 800);
      } else {
        const errText = await res.text();
        triggerNotification('error', errText || 'Parsing failed');
        setUploading(false);
      }
    } catch (err) {
      clearInterval(interval);
      setUploading(false);
      triggerNotification('error', 'Network error. Backend might be down.');
    }
  };

  return (
    <motion.div initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} style={{ padding: '2rem', maxWidth: '900px', margin: '0 auto' }}>
      <h1 style={{ fontSize: '2.5rem', marginBottom: '1rem', textAlign: 'center' }}>Gemini Extraction Engine</h1>
      <p style={{ textAlign: 'center', marginBottom: '3rem', fontSize: '1.1rem' }}>Upload a PDF, DOCX, or TXT. AI will extract structured metadata instantly.</p>
      
      <div className="glass-panel" style={{ padding: '3rem', textAlign: 'center', border: dragActive ? '2px dashed var(--primary)' : '2px dashed var(--border-color)', transition: 'all 0.3s ease' }}
           onDragEnter={handleDrag} onDragOver={handleDrag} onDragLeave={handleDrag} onDrop={handleDrop}>
        
        {!uploading ? (
          <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '1rem' }}>
            <div style={{ padding: '1.5rem', background: 'rgba(99, 102, 241, 0.1)', borderRadius: '50%', color: 'var(--primary)' }}>
              <UploadCloud size={48} />
            </div>
            <h3 style={{ fontSize: '1.5rem' }}>Drag & drop resume here</h3>
            <p>or click to browse from your computer</p>
            <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Supports PDF, DOCX, TXT up to 10MB</p>
            <input type="file" style={{ display: 'none' }} id="file-upload" accept=".pdf,.docx,.txt" onChange={(e) => { if (e.target.files[0]) uploadFile(e.target.files[0]); }} />
            <button className="btn btn-primary" style={{ marginTop: '1rem' }} onClick={() => document.getElementById('file-upload').click()}>
              Select File
            </button>
          </div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '2rem', padding: '2rem 0' }}>
            <div className="spinner" style={{ width: '60px', height: '60px', borderWidth: '4px' }}></div>
            <div>
              <h3 style={{ fontSize: '1.5rem', marginBottom: '0.5rem' }}>{progress === 100 ? 'Finalizing...' : 'AI Processing...'}</h3>
              <p>Analyzing document structure and extracting key entities.</p>
            </div>
            <div style={{ width: '80%', height: '8px', background: 'var(--bg-secondary)', borderRadius: '4px', overflow: 'hidden' }}>
              <motion.div 
                initial={{ width: 0 }}
                animate={{ width: `${progress}%` }}
                style={{ height: '100%', background: 'var(--primary)' }}
              />
            </div>
          </div>
        )}
      </div>
    </motion.div>
  );
}
