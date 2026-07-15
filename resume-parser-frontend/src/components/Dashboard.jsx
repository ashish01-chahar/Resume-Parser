import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { FileText, Code, Database, Search, Eye, Trash2, Mail, Phone } from 'lucide-react';

export default function Dashboard({ setView, setSelectedCandidateId, triggerNotification }) {
  const [stats, setStats] = useState(null);
  const [candidates, setCandidates] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterSkill, setFilterSkill] = useState('');
  const [allSkills, setAllSkills] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const statsRes = await fetch('http://localhost:8080/api/dashboard/stats');
        if (statsRes.ok) setStats(await statsRes.json());
        
        const candsRes = await fetch('http://localhost:8080/api/candidates');
        if (candsRes.ok) {
            const data = await candsRes.json();
            setCandidates(data);
            
            // Extract unique skills
            const skills = new Set();
            data.forEach(c => c.skills?.forEach(s => skills.add(s.name)));
            setAllSkills(Array.from(skills).sort());
        }
      } catch (err) {
        triggerNotification('error', 'Failed to fetch dashboard data. Ensure backend is running.');
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [triggerNotification]);

  const handleDelete = async (e, id) => {
    e.stopPropagation();
    if (!window.confirm('Delete this candidate?')) return;
    try {
      const res = await fetch(`http://localhost:8080/api/candidates/${id}`, { method: 'DELETE' });
      if (res.ok) {
        setCandidates(candidates.filter(c => c.id !== id));
        triggerNotification('success', 'Candidate deleted');
      }
    } catch (err) {
      triggerNotification('error', 'Error deleting candidate');
    }
  };

  const filteredCandidates = candidates.filter(c => {
    const matchesSearch = c.fullName?.toLowerCase().includes(searchTerm.toLowerCase()) || 
                          c.email?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                          c.skills?.some(s => s.name.toLowerCase().includes(searchTerm.toLowerCase()));
    const matchesSkill = filterSkill ? c.skills?.some(s => s.name === filterSkill) : true;
    return matchesSearch && matchesSkill;
  });

  if (loading) return <div style={{ padding: '2rem' }}>Loading dashboard...</div>;

  return (
    <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} style={{ padding: '2rem' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end', marginBottom: '2rem' }}>
        <div>
          <h1 style={{ fontSize: '2rem', marginBottom: '0.5rem' }}>Talent Directory</h1>
          <p>Overview of parsed candidates and system metrics.</p>
        </div>
        <button className="btn btn-primary" onClick={() => setView('upload')}>
          Parse New Resume
        </button>
      </div>

      {stats && (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '1.5rem', marginBottom: '3rem' }}>
          <div className="glass-panel" style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
            <div style={{ padding: '1rem', background: 'rgba(99, 102, 241, 0.1)', borderRadius: '12px', color: 'var(--primary)' }}>
              <FileText size={28} />
            </div>
            <div>
              <p style={{ fontSize: '0.9rem', marginBottom: '0.25rem' }}>Total Candidates</p>
              <h3 style={{ fontSize: '1.8rem' }}>{stats.totalCandidates}</h3>
            </div>
          </div>
          <div className="glass-panel" style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
            <div style={{ padding: '1rem', background: 'rgba(6, 182, 212, 0.1)', borderRadius: '12px', color: 'var(--accent)' }}>
              <Code size={28} />
            </div>
            <div>
              <p style={{ fontSize: '0.9rem', marginBottom: '0.25rem' }}>Unique Tech Skills</p>
              <h3 style={{ fontSize: '1.8rem' }}>{stats.uniqueSkills}</h3>
            </div>
          </div>
          <div className="glass-panel" style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
            <div style={{ padding: '1rem', background: 'rgba(16, 185, 129, 0.1)', borderRadius: '12px', color: 'var(--success)' }}>
              <Database size={28} />
            </div>
            <div>
              <p style={{ fontSize: '0.9rem', marginBottom: '0.25rem' }}>Database Node</p>
              <h3 style={{ fontSize: '1.2rem', marginTop: '0.5rem' }}>{stats.databaseStatus}</h3>
            </div>
          </div>
        </div>
      )}

      <div className="glass-panel" style={{ padding: '0', overflow: 'hidden' }}>
        <div style={{ padding: '1.5rem', display: 'flex', gap: '1rem', borderBottom: '1px solid var(--border-color)' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', flex: 1, background: 'var(--bg-primary)', padding: '0.5rem 1rem', borderRadius: '8px', border: '1px solid var(--border-color)' }}>
            <Search size={18} color="var(--text-muted)" />
            <input 
              type="text" 
              placeholder="Search candidate, email, or skill..." 
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              style={{ background: 'transparent', border: 'none', color: 'var(--text-primary)', outline: 'none', width: '100%' }}
            />
          </div>
          <select 
            value={filterSkill}
            onChange={(e) => setFilterSkill(e.target.value)}
            style={{ background: 'var(--bg-primary)', border: '1px solid var(--border-color)', color: 'var(--text-primary)', padding: '0.5rem 1rem', borderRadius: '8px', outline: 'none' }}
          >
            <option value="">All Skills</option>
            {allSkills.map(sk => <option key={sk} value={sk}>{sk}</option>)}
          </select>
        </div>
        
        <div className="table-container">
          {filteredCandidates.length > 0 ? (
            <table>
              <thead>
                <tr>
                  <th>Candidate</th>
                  <th>Contact Details</th>
                  <th>Top Skills</th>
                  <th>Parsed Date</th>
                  <th style={{ width: '100px' }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {filteredCandidates.map(c => (
                  <tr key={c.id} style={{ cursor: 'pointer' }} onClick={() => { setSelectedCandidateId(c.id); setView('details'); }}>
                    <td>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                        <div style={{ width: '40px', height: '40px', borderRadius: '8px', background: 'var(--primary)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#fff', fontWeight: 'bold' }}>
                          {c.fullName ? c.fullName.substring(0, 2).toUpperCase() : 'U'}
                        </div>
                        <div>
                          <div style={{ fontWeight: 600 }}>{c.fullName || 'Unknown Candidate'}</div>
                          <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>{c.experience?.length || 0} work entries</div>
                        </div>
                      </div>
                    </td>
                    <td>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.85rem', marginBottom: '0.25rem' }}>
                        <Mail size={14} color="var(--accent)" /> {c.email || 'N/A'}
                      </div>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.85rem' }}>
                        <Phone size={14} color="var(--accent)" /> {c.phone || 'N/A'}
                      </div>
                    </td>
                    <td>
                      <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
                        {c.skills?.slice(0, 3).map((s, idx) => (
                          <span key={idx} className="tag">{s.name}</span>
                        ))}
                        {c.skills?.length > 3 && (
                          <span className="tag" style={{ background: 'transparent' }}>+{c.skills.length - 3}</span>
                        )}
                      </div>
                    </td>
                    <td style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
                      {new Date(c.createdAt).toLocaleDateString()}
                    </td>
                    <td>
                      <div style={{ display: 'flex', gap: '0.5rem' }}>
                        <button className="icon-btn" onClick={(e) => { e.stopPropagation(); setSelectedCandidateId(c.id); setView('details'); }} style={{ padding: '0.5rem', background: 'transparent', border: 'none', color: 'var(--text-secondary)', cursor: 'pointer' }}>
                          <Eye size={18} />
                        </button>
                        <button className="icon-btn" onClick={(e) => handleDelete(e, c.id)} style={{ padding: '0.5rem', background: 'transparent', border: 'none', color: 'var(--danger)', cursor: 'pointer' }}>
                          <Trash2 size={18} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <div style={{ padding: '4rem 2rem', textAlign: 'center', color: 'var(--text-muted)' }}>
              <FileText size={48} style={{ margin: '0 auto 1rem', opacity: 0.5 }} />
              <h3>No Candidates Found</h3>
              <p style={{ marginTop: '0.5rem' }}>Upload a resume to populate the database.</p>
            </div>
          )}
        </div>
      </div>
    </motion.div>
  );
}
