import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { ArrowLeft, User, Mail, Phone, MapPin, Download, Trash2, Code, Briefcase, GraduationCap } from 'lucide-react';

export default function CandidateDetails({ candidateId, setView, triggerNotification }) {
  const [candidate, setCandidate] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchCandidate = async () => {
      try {
        const res = await fetch(`http://localhost:8080/api/candidates/${candidateId}`);
        if (res.ok) setCandidate(await res.json());
        else triggerNotification('error', 'Candidate not found');
      } catch (err) {
        triggerNotification('error', 'Error fetching candidate details');
      } finally {
        setLoading(false);
      }
    };
    if (candidateId) fetchCandidate();
  }, [candidateId, triggerNotification]);

  const handleDelete = async () => {
    if (!window.confirm('Delete this candidate forever?')) return;
    try {
      const res = await fetch(`http://localhost:8080/api/candidates/${candidateId}`, { method: 'DELETE' });
      if (res.ok) {
        triggerNotification('success', 'Candidate deleted');
        setView('dashboard');
      }
    } catch (err) {
      triggerNotification('error', 'Failed to delete');
    }
  };

  const handleDownloadFile = () => {
    window.open(`http://localhost:8080/api/candidates/${candidateId}/download`, '_blank');
  };

  const handleDownloadJSON = () => {
    const dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(candidate, null, 2));
    const downloadAnchorNode = document.createElement('a');
    downloadAnchorNode.setAttribute("href",     dataStr);
    downloadAnchorNode.setAttribute("download", `${candidate.fullName || 'candidate'}.json`);
    document.body.appendChild(downloadAnchorNode);
    downloadAnchorNode.click();
    downloadAnchorNode.remove();
  };

  if (loading) return <div style={{ padding: '2rem' }}>Loading details...</div>;
  if (!candidate) return <div style={{ padding: '2rem' }}>Candidate not found.</div>;

  return (
    <motion.div initial={{ opacity: 0, x: 20 }} animate={{ opacity: 1, x: 0 }} style={{ padding: '2rem', maxWidth: '1200px', margin: '0 auto' }}>
      
      {/* Header Actions */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
        <button className="btn btn-secondary" onClick={() => setView('dashboard')}>
          <ArrowLeft size={16} /> Back to Directory
        </button>
        <div style={{ display: 'flex', gap: '1rem' }}>
          <button className="btn btn-secondary" onClick={handleDownloadJSON}>
            <Code size={16} /> Export JSON
          </button>
          <button className="btn btn-primary" onClick={handleDownloadFile}>
            <Download size={16} /> Original Resume
          </button>
          <button className="btn btn-secondary" onClick={handleDelete} style={{ color: 'var(--danger)', borderColor: 'var(--danger)' }}>
            <Trash2 size={16} /> Delete
          </button>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 350px', gap: '2rem' }}>
        {/* Main Content Column */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
          
          {/* Summary Card */}
          <div className="glass-panel" style={{ padding: '2rem' }}>
            <div style={{ display: 'flex', gap: '1.5rem', alignItems: 'center', marginBottom: '1.5rem' }}>
              <div style={{ width: '80px', height: '80px', borderRadius: '16px', background: 'var(--primary)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#fff', fontSize: '2rem', fontWeight: 'bold' }}>
                {candidate.fullName ? candidate.fullName.substring(0, 2).toUpperCase() : 'U'}
              </div>
              <div>
                <h1 style={{ fontSize: '2.2rem', marginBottom: '0.25rem' }}>{candidate.fullName || 'Unknown Candidate'}</h1>
                <div style={{ display: 'flex', gap: '1.5rem', color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
                  <span style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}><Mail size={14} /> {candidate.email || 'No email'}</span>
                  <span style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}><Phone size={14} /> {candidate.phone || 'No phone'}</span>
                  <span style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}><MapPin size={14} /> {candidate.location || 'No location'}</span>
                </div>
              </div>
            </div>
            
            {candidate.summary && (
              <div>
                <h3 style={{ fontSize: '1.1rem', marginBottom: '0.5rem', color: 'var(--text-primary)' }}>Professional Summary</h3>
                <p style={{ lineHeight: '1.7' }}>{candidate.summary}</p>
              </div>
            )}
          </div>

          {/* Experience */}
          {candidate.experience && candidate.experience.length > 0 && (
            <div className="glass-panel">
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1.5rem' }}>
                <Briefcase color="var(--primary)" />
                <h2>Work Experience</h2>
              </div>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
                {candidate.experience.map((exp, idx) => (
                  <div key={idx} style={{ paddingLeft: '1.5rem', borderLeft: '2px solid var(--border-color)', position: 'relative' }}>
                    <div style={{ position: 'absolute', left: '-6px', top: '0', width: '10px', height: '10px', borderRadius: '50%', background: 'var(--primary)' }}></div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.5rem' }}>
                      <div>
                        <h3 style={{ fontSize: '1.1rem' }}>{exp.role}</h3>
                        <div style={{ color: 'var(--primary)', fontWeight: 600 }}>{exp.company}</div>
                      </div>
                      <span className="tag">{exp.duration}</span>
                    </div>
                    <p style={{ fontSize: '0.95rem' }}>{exp.description}</p>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Education */}
          {candidate.education && candidate.education.length > 0 && (
            <div className="glass-panel">
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1.5rem' }}>
                <GraduationCap color="var(--primary)" />
                <h2>Education</h2>
              </div>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
                {candidate.education.map((edu, idx) => (
                  <div key={idx} style={{ paddingLeft: '1.5rem', borderLeft: '2px solid var(--border-color)', position: 'relative' }}>
                    <div style={{ position: 'absolute', left: '-6px', top: '0', width: '10px', height: '10px', borderRadius: '50%', background: 'var(--primary)' }}></div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                      <div>
                        <h3 style={{ fontSize: '1.1rem' }}>{edu.degree}</h3>
                        <div style={{ color: 'var(--text-secondary)' }}>{edu.institution}</div>
                      </div>
                      <span className="tag">{edu.duration}</span>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>

        {/* Right Sidebar Column */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
          
          {/* Skills */}
          <div className="glass-panel">
            <h3 style={{ marginBottom: '1rem' }}>Technical Skills</h3>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
              {candidate.skills?.length > 0 ? (
                candidate.skills.map((s, idx) => <span key={idx} className="tag">{s.name}</span>)
              ) : (
                <p>No skills extracted</p>
              )}
            </div>
          </div>

          <div className="glass-panel">
            <h3 style={{ marginBottom: '1rem' }}>Soft Skills</h3>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
              {candidate.softSkills?.length > 0 ? (
                candidate.softSkills.map((s, idx) => <span key={idx} className="tag">{s.name}</span>)
              ) : (
                <p>No soft skills extracted</p>
              )}
            </div>
          </div>

          <div className="glass-panel">
            <h3 style={{ marginBottom: '1rem' }}>Raw JSON</h3>
            <div style={{ 
              background: 'var(--bg-primary)', 
              padding: '1rem', 
              borderRadius: '8px', 
              fontFamily: 'monospace', 
              fontSize: '0.85rem', 
              color: 'var(--text-secondary)',
              maxHeight: '400px',
              overflowY: 'auto',
              border: '1px solid var(--border-color)'
            }}>
              <pre>{JSON.stringify(JSON.parse(candidate.rawJson || '{}'), null, 2)}</pre>
            </div>
          </div>
        </div>
      </div>
    </motion.div>
  );
}
