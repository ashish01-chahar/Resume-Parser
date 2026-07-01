import React, { useState, useEffect } from 'react';
import { 
  UploadCloud, 
  FileText, 
  Trash2, 
  Eye, 
  Plus, 
  X, 
  Search, 
  Database, 
  Sparkles, 
  GraduationCap, 
  Briefcase, 
  Code, 
  Mail, 
  Phone, 
  User,
  ArrowLeft
} from 'lucide-react';

const BACKEND_URL = 'http://localhost:8080/api/resumes';

function App() {
  const [view, setView] = useState('dashboard'); // 'dashboard' | 'upload' | 'editor'
  const [resumes, setResumes] = useState([]);
  const [selectedResume, setSelectedResume] = useState(null);
  const [uploading, setUploading] = useState(false);
  const [progress, setProgress] = useState(0);
  const [error, setError] = useState('');
  const [successMsg, setSuccessMsg] = useState('');
  
  // Search & Filters
  const [searchTerm, setSearchTerm] = useState('');
  const [filterSkill, setFilterSkill] = useState('');
  
  // Input helpers for editor
  const [newSkill, setNewSkill] = useState('');

  // Fetch resumes from backend
  const fetchResumes = async () => {
    try {
      const response = await fetch(BACKEND_URL);
      if (response.ok) {
        const data = await response.json();
        setResumes(data);
      } else {
        setError('Failed to fetch resumes from database.');
      }
    } catch (err) {
      console.error(err);
      setError('Could not connect to backend server. Make sure Spring Boot is running on port 8080.');
    }
  };

  useEffect(() => {
    fetchResumes();
  }, []);

  // Show a success/error message with auto-hide
  const triggerNotification = (type, text) => {
    if (type === 'error') {
      setError(text);
      setTimeout(() => setError(''), 5000);
    } else {
      setSuccessMsg(text);
      setTimeout(() => setSuccessMsg(''), 5000);
    }
  };

  // Delete resume
  const handleDelete = async (e, id) => {
    e.stopPropagation();
    if (!window.confirm('Are you sure you want to delete this resume?')) return;
    try {
      const response = await fetch(`${BACKEND_URL}/${id}`, {
        method: 'DELETE',
      });
      if (response.ok) {
        setResumes(resumes.filter(r => r.id !== id));
        triggerNotification('success', 'Resume deleted successfully!');
      } else {
        triggerNotification('error', 'Failed to delete resume.');
      }
    } catch (err) {
      triggerNotification('error', 'Error connecting to server.');
    }
  };

  // Handle Drag & Drop
  const [dragActive, setDragActive] = useState(false);
  
  const handleDrag = (e) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.type === "dragenter" || e.type === "dragover") {
      setDragActive(true);
    } else if (e.type === "dragleave") {
      setDragActive(false);
    }
  };

  const handleDrop = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);
    
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      uploadFile(e.dataTransfer.files[0]);
    }
  };

  const handleFileChange = (e) => {
    if (e.target.files && e.target.files[0]) {
      uploadFile(e.target.files[0]);
    }
  };

  // Upload and Parse
  const uploadFile = async (file) => {
    setUploading(true);
    setProgress(20);
    setError('');
    
    const formData = new FormData();
    formData.append('file', file);
    
    try {
      setProgress(50);
      const response = await fetch(`${BACKEND_URL}/upload`, {
        method: 'POST',
        body: formData,
      });
      
      setProgress(80);
      if (response.ok) {
        const parsedData = await response.json();
        // The parsed object might have empty list nodes, initialize if null
        parsedData.skills = parsedData.skills || [];
        parsedData.education = parsedData.education || [];
        parsedData.experience = parsedData.experience || [];
        
        setSelectedResume(parsedData);
        setView('editor');
        setProgress(100);
        triggerNotification('success', 'Resume parsed successfully! Review before saving.');
      } else {
        const text = await response.text();
        triggerNotification('error', text || 'Failed to parse resume.');
      }
    } catch (err) {
      triggerNotification('error', 'Failed to reach backend parser. Please verify the Spring Boot service is running.');
    } finally {
      setUploading(false);
      setProgress(0);
    }
  };

  // Save reviewed resume
  const handleSaveResume = async () => {
    try {
      const response = await fetch(BACKEND_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(selectedResume)
      });
      
      if (response.ok) {
        triggerNotification('success', 'Resume saved to database!');
        fetchResumes();
        setView('dashboard');
        setSelectedResume(null);
      } else {
        triggerNotification('error', 'Failed to save resume to database.');
      }
    } catch (err) {
      triggerNotification('error', 'Connection error saving to server.');
    }
  };

  // Editor modification helpers
  const handleGeneralChange = (field, val) => {
    setSelectedResume({ ...selectedResume, [field]: val });
  };

  const handleAddSkill = () => {
    if (!newSkill.trim()) return;
    const skillsList = [...selectedResume.skills];
    if (!skillsList.some(s => s.name.toLowerCase() === newSkill.trim().toLowerCase())) {
      skillsList.push({ name: newSkill.trim() });
    }
    setSelectedResume({ ...selectedResume, skills: skillsList });
    setNewSkill('');
  };

  const handleRemoveSkill = (skillIndex) => {
    const skillsList = selectedResume.skills.filter((_, idx) => idx !== skillIndex);
    setSelectedResume({ ...selectedResume, skills: skillsList });
  };

  const handleAddEducation = () => {
    const eduList = [...selectedResume.education];
    eduList.push({ degree: '', institution: '', duration: '' });
    setSelectedResume({ ...selectedResume, education: eduList });
  };

  const handleEduChange = (index, field, val) => {
    const eduList = [...selectedResume.education];
    eduList[index][field] = val;
    setSelectedResume({ ...selectedResume, education: eduList });
  };

  const handleRemoveEdu = (index) => {
    const eduList = selectedResume.education.filter((_, idx) => idx !== index);
    setSelectedResume({ ...selectedResume, education: eduList });
  };

  const handleAddExperience = () => {
    const expList = [...selectedResume.experience];
    expList.push({ company: '', role: '', duration: '', description: '' });
    setSelectedResume({ ...selectedResume, experience: expList });
  };

  const handleExpChange = (index, field, val) => {
    const expList = [...selectedResume.experience];
    expList[index][field] = val;
    setSelectedResume({ ...selectedResume, experience: expList });
  };

  const handleRemoveExp = (index) => {
    const expList = selectedResume.experience.filter((_, idx) => idx !== index);
    setSelectedResume({ ...selectedResume, experience: expList });
  };

  // Extract all unique skills across all resumes for filter list
  const allSkills = Array.from(
    new Set(resumes.flatMap(r => r.skills.map(s => s.name)))
  ).sort();

  // Search & filter logic
  const filteredResumes = resumes.filter(r => {
    const matchesSearch = 
      r.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      r.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
      r.phone.toLowerCase().includes(searchTerm.toLowerCase()) ||
      r.skills.some(s => s.name.toLowerCase().includes(searchTerm.toLowerCase()));
      
    const matchesSkill = filterSkill ? r.skills.some(s => s.name === filterSkill) : true;
    
    return matchesSearch && matchesSkill;
  });

  return (
    <div className="app-container">
      {/* Navbar */}
      <header className="navbar">
        <div className="logo-container">
          <Sparkles className="logo-icon" />
          <span>ResumeIntellect</span>
        </div>
        <nav className="nav-links">
          <button 
            className={`nav-btn ${view === 'dashboard' ? 'active' : ''}`}
            onClick={() => { setView('dashboard'); setSelectedResume(null); }}
          >
            <Database size={16} />
            Dashboard
          </button>
          <button 
            className={`nav-btn ${view === 'upload' ? 'active' : ''}`}
            onClick={() => setView('upload')}
          >
            <UploadCloud size={16} />
            Parse Resume
          </button>
        </nav>
      </header>

      {/* Main Content */}
      <main className="main-content">
        {error && (
          <div className="alert-banner error">
            <X size={18} />
            {error}
          </div>
        )}
        {successMsg && (
          <div className="alert-banner success">
            <Sparkles size={18} />
            {successMsg}
          </div>
        )}

        {/* --- DASHBOARD VIEW --- */}
        {view === 'dashboard' && (
          <div>
            <h1>Talent Directory</h1>
            <p className="subtitle">View and filter resumes parsed by the AI processing core.</p>

            {/* Metrics cards */}
            <div className="metrics-grid">
              <div className="glass-panel metric-card">
                <div className="metric-icon-wrapper">
                  <FileText size={24} />
                </div>
                <div className="metric-info">
                  <h4>Parsed Resumes</h4>
                  <div className="value">{resumes.length}</div>
                </div>
              </div>
              <div className="glass-panel metric-card cyan">
                <div className="metric-icon-wrapper">
                  <Code size={24} />
                </div>
                <div className="metric-info">
                  <h4>Unique Tech Skills</h4>
                  <div className="value">{allSkills.length}</div>
                </div>
              </div>
              <div className="glass-panel metric-card success">
                <div className="metric-icon-wrapper">
                  <Database size={24} />
                </div>
                <div className="metric-info">
                  <h4>Database Node</h4>
                  <div className="value">MySQL 3306</div>
                </div>
              </div>
            </div>

            {/* Search and Filters */}
            <div className="glass-panel" style={{ marginBottom: '2rem' }}>
              <div className="dashboard-actions">
                <div className="search-input-wrapper">
                  <Search className="search-icon" />
                  <input 
                    type="text" 
                    placeholder="Search candidate, email, or skill..." 
                    className="search-input"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                  />
                </div>
                <div className="filters-wrapper">
                  <select 
                    className="filter-select"
                    value={filterSkill}
                    onChange={(e) => setFilterSkill(e.target.value)}
                  >
                    <option value="">All Skills</option>
                    {allSkills.map(sk => (
                      <option key={sk} value={sk}>{sk}</option>
                    ))}
                  </select>
                </div>
              </div>

              {/* Table */}
              <div className="resume-list-container">
                {filteredResumes.length > 0 ? (
                  <table className="resume-table">
                    <thead>
                      <tr>
                        <th>Candidate</th>
                        <th>Contact Details</th>
                        <th>Extracted Skills</th>
                        <th>Uploaded Date</th>
                        <th style={{ width: '100px' }}>Actions</th>
                      </tr>
                    </thead>
                    <tbody>
                      {filteredResumes.map(r => (
                        <tr 
                          key={r.id} 
                          className="resume-row"
                          onClick={() => { setSelectedResume(r); setView('editor'); }}
                        >
                          <td>
                            <div className="candidate-name-wrapper">
                              <div className="avatar">
                                {r.name ? r.name.substring(0, 2) : 'UC'}
                              </div>
                              <div>
                                <div style={{ fontWeight: 700 }}>{r.name || 'Unknown Candidate'}</div>
                                <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                                  {r.experience?.length || 0} work entries
                                </div>
                              </div>
                            </div>
                          </td>
                          <td>
                            <div className="flex-gap-1" style={{ fontSize: '0.85rem' }}>
                              <Mail size={14} className="text-cyan" />
                              <span>{r.email || 'N/A'}</span>
                            </div>
                            <div className="flex-gap-1" style={{ fontSize: '0.85rem', marginTop: '0.25rem' }}>
                              <Phone size={14} className="text-cyan" />
                              <span>{r.phone || 'N/A'}</span>
                            </div>
                          </td>
                          <td>
                            <div className="tag-list">
                              {r.skills && r.skills.length > 0 ? (
                                r.skills.slice(0, 6).map((s, idx) => (
                                  <span key={idx} className="skill-tag">{s.name}</span>
                                ))
                              ) : (
                                <span style={{ color: 'var(--text-muted)', fontSize: '0.8rem' }}>None</span>
                              )}
                              {r.skills?.length > 6 && (
                                <span className="skill-tag" style={{ background: 'rgba(255,255,255,0.05)', color: '#fff' }}>
                                  +{r.skills.length - 6} more
                                </span>
                              )}
                            </div>
                          </td>
                          <td style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
                            {r.createdAt ? new Date(r.createdAt).toLocaleDateString() : 'N/A'}
                          </td>
                          <td>
                            <div className="actions-cell">
                              <button 
                                className="icon-btn"
                                title="View Details"
                                onClick={(e) => { e.stopPropagation(); setSelectedResume(r); setView('editor'); }}
                              >
                                <Eye size={18} />
                              </button>
                              <button 
                                className="icon-btn delete"
                                title="Delete Resume"
                                onClick={(e) => handleDelete(e, r.id)}
                              >
                                <Trash2 size={18} />
                              </button>
                            </div>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                ) : (
                  <div className="empty-state">
                    <FileText className="empty-icon" />
                    <h3>No Resumes Found</h3>
                    <p>There are no resumes in the directory matching your search criteria.</p>
                    <button className="btn btn-primary" onClick={() => setView('upload')}>
                      Parse A Resume
                    </button>
                  </div>
                )}
              </div>
            </div>
          </div>
        )}

        {/* --- UPLOAD VIEW --- */}
        {view === 'upload' && (
          <div className="upload-wrapper">
            <h1>Parse Engine</h1>
            <p className="subtitle">Upload a PDF, DOCX, or TXT resume. The parser will extract key metadata.</p>
            
            <div className="glass-panel">
              {!uploading ? (
                <div 
                  className={`upload-zone ${dragActive ? 'dragging' : ''}`}
                  onDragEnter={handleDrag}
                  onDragOver={handleDrag}
                  onDragLeave={handleDrag}
                  onDrop={handleDrop}
                  onClick={() => document.getElementById('resume-file').click()}
                >
                  <UploadCloud className="upload-icon" />
                  <div>
                    <div className="upload-text">Drag and drop resume here</div>
                    <div className="upload-desc" style={{ marginTop: '0.25rem' }}>or click to browse from files</div>
                  </div>
                  <div className="upload-desc">Supports PDF, DOCX, and TXT up to 10MB</div>
                  <input 
                    type="file" 
                    id="resume-file"
                    className="file-input"
                    accept=".pdf,.docx,.txt"
                    onChange={handleFileChange}
                  />
                </div>
              ) : (
                <div className="uploading-animation" style={{ padding: '2rem 0' }}>
                  <div className="spinner"></div>
                  <div className="upload-text" style={{ marginTop: '1rem' }}>Extracting details...</div>
                  <div className="upload-desc">Apache Tika is scanning document structures.</div>
                  <div className="progress-container" style={{ width: '80%' }}>
                    <div className="progress-bar" style={{ width: `${progress}%` }}></div>
                  </div>
                </div>
              )}

              <div className="button-group" style={{ marginTop: '2rem' }}>
                <button className="btn btn-secondary" onClick={() => setView('dashboard')}>
                  Cancel
                </button>
              </div>
            </div>
          </div>
        )}

        {/* --- EDITOR / REVIEW VIEW --- */}
        {view === 'editor' && selectedResume && (
          <div>
            <div className="flex-between" style={{ marginBottom: '1.5rem' }}>
              <div>
                <button className="btn btn-secondary" onClick={() => { setView('dashboard'); setSelectedResume(null); }} style={{ padding: '0.5rem 1rem', fontSize: '0.85rem' }}>
                  <ArrowLeft size={16} />
                  Back
                </button>
              </div>
              <h1 style={{ margin: 0, fontSize: '2rem' }}>Candidate Metadata Review</h1>
              <div>
                {selectedResume.id ? (
                  <span className="skill-tag" style={{ background: 'var(--success)', color: '#fff', padding: '0.4rem 1rem', fontSize: '0.85rem' }}>
                    Saved
                  </span>
                ) : (
                  <span className="skill-tag" style={{ background: 'var(--warning)', color: '#fff', padding: '0.4rem 1rem', fontSize: '0.85rem' }}>
                    Draft (Unsaved)
                  </span>
                )}
              </div>
            </div>
            
            <div className="editor-layout">
              {/* Left Pane - Editable fields */}
              <div className="glass-panel">
                <div className="pane-title">
                  <h2>Extracted Profile</h2>
                  <User className="text-cyan" />
                </div>

                {/* General Information */}
                <div className="form-grid">
                  <div className="form-group">
                    <label className="form-label">Full Name</label>
                    <input 
                      type="text" 
                      className="form-input" 
                      value={selectedResume.name || ''} 
                      onChange={(e) => handleGeneralChange('name', e.target.value)}
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Email Address</label>
                    <input 
                      type="email" 
                      className="form-input" 
                      value={selectedResume.email || ''} 
                      onChange={(e) => handleGeneralChange('email', e.target.value)}
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Phone Number</label>
                    <input 
                      type="text" 
                      className="form-input" 
                      value={selectedResume.phone || ''} 
                      onChange={(e) => handleGeneralChange('phone', e.target.value)}
                    />
                  </div>
                </div>

                {/* Skills Section */}
                <div className="nested-section">
                  <div className="section-header">
                    <h3>Technical Skills</h3>
                    <Code className="text-cyan" size={18} />
                  </div>
                  
                  <div className="skills-input-container">
                    <input 
                      type="text" 
                      className="form-input" 
                      placeholder="Add tech skill (e.g. Docker, Python)..." 
                      value={newSkill}
                      onChange={(e) => setNewSkill(e.target.value)}
                      onKeyDown={(e) => e.key === 'Enter' && handleAddSkill()}
                      style={{ flex: 1 }}
                    />
                    <button className="btn btn-primary" onClick={handleAddSkill} style={{ padding: '0 1rem' }}>
                      <Plus size={18} />
                    </button>
                  </div>
                  
                  <div className="tags-editor">
                    {selectedResume.skills.map((sk, idx) => (
                      <span key={idx} className="skill-edit-tag">
                        {sk.name}
                        <button className="tag-remove-btn" onClick={() => handleRemoveSkill(idx)}>
                          <X size={12} />
                        </button>
                      </span>
                    ))}
                    {selectedResume.skills.length === 0 && (
                      <span style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>No skills added.</span>
                    )}
                  </div>
                </div>

                {/* Education Section */}
                <div className="nested-section">
                  <div className="section-header">
                    <h3>Education History</h3>
                    <button className="btn btn-secondary" onClick={handleAddEducation} style={{ padding: '0.4rem 0.8rem', fontSize: '0.8rem' }}>
                      <Plus size={14} /> Add Education
                    </button>
                  </div>

                  {selectedResume.education.map((edu, idx) => (
                    <div key={idx} className="nested-item-card">
                      <button className="card-remove-btn" onClick={() => handleRemoveEdu(idx)}>
                        <X size={18} />
                      </button>
                      <div className="form-grid" style={{ marginBottom: 0 }}>
                        <div className="form-group">
                          <label className="form-label">Degree / Certificate</label>
                          <input 
                            type="text" 
                            className="form-input" 
                            placeholder="e.g. B.Tech Computer Science"
                            value={edu.degree || ''} 
                            onChange={(e) => handleEduChange(idx, 'degree', e.target.value)}
                          />
                        </div>
                        <div className="form-group">
                          <label className="form-label">Institution / University</label>
                          <input 
                            type="text" 
                            className="form-input" 
                            placeholder="e.g. Stanford University"
                            value={edu.institution || ''} 
                            onChange={(e) => handleEduChange(idx, 'institution', e.target.value)}
                          />
                        </div>
                        <div className="form-group full-width">
                          <label className="form-label">Duration</label>
                          <input 
                            type="text" 
                            className="form-input" 
                            placeholder="e.g. 2018 - 2022"
                            value={edu.duration || ''} 
                            onChange={(e) => handleEduChange(idx, 'duration', e.target.value)}
                          />
                        </div>
                      </div>
                    </div>
                  ))}
                  {selectedResume.education.length === 0 && (
                    <div style={{ color: 'var(--text-muted)', fontSize: '0.85rem', textAlign: 'center', padding: '1rem 0' }}>
                      No education entries.
                    </div>
                  )}
                </div>

                {/* Experience Section */}
                <div className="nested-section">
                  <div className="section-header">
                    <h3>Work Experience</h3>
                    <button className="btn btn-secondary" onClick={handleAddExperience} style={{ padding: '0.4rem 0.8rem', fontSize: '0.8rem' }}>
                      <Plus size={14} /> Add Experience
                    </button>
                  </div>

                  {selectedResume.experience.map((exp, idx) => (
                    <div key={idx} className="nested-item-card">
                      <button className="card-remove-btn" onClick={() => handleRemoveExp(idx)}>
                        <X size={18} />
                      </button>
                      <div className="form-grid">
                        <div className="form-group">
                          <label className="form-label">Company Name</label>
                          <input 
                            type="text" 
                            className="form-input" 
                            placeholder="e.g. Google"
                            value={exp.company || ''} 
                            onChange={(e) => handleExpChange(idx, 'company', e.target.value)}
                          />
                        </div>
                        <div className="form-group">
                          <label className="form-label">Job Role / Title</label>
                          <input 
                            type="text" 
                            className="form-input" 
                            placeholder="e.g. Frontend Engineer"
                            value={exp.role || ''} 
                            onChange={(e) => handleExpChange(idx, 'role', e.target.value)}
                          />
                        </div>
                        <div className="form-group full-width">
                          <label className="form-label">Duration</label>
                          <input 
                            type="text" 
                            className="form-input" 
                            placeholder="e.g. 2022 - Present"
                            value={exp.duration || ''} 
                            onChange={(e) => handleExpChange(idx, 'duration', e.target.value)}
                          />
                        </div>
                        <div className="form-group full-width">
                          <label className="form-label">Role Description</label>
                          <textarea 
                            className="form-input" 
                            rows={3}
                            placeholder="Describe duties, technologies used..."
                            value={exp.description || ''} 
                            onChange={(e) => handleExpChange(idx, 'description', e.target.value)}
                            style={{ resize: 'vertical', fontFamily: 'inherit' }}
                          />
                        </div>
                      </div>
                    </div>
                  ))}
                  {selectedResume.experience.length === 0 && (
                    <div style={{ color: 'var(--text-muted)', fontSize: '0.85rem', textAlign: 'center', padding: '1rem 0' }}>
                      No experience entries.
                    </div>
                  )}
                </div>

                {/* Actions Footer */}
                <div className="button-group">
                  <button className="btn btn-secondary" onClick={() => { setView('dashboard'); setSelectedResume(null); }}>
                    Cancel
                  </button>
                  {!selectedResume.id && (
                    <button className="btn btn-primary" onClick={handleSaveResume}>
                      <Database size={16} />
                      Save to Database
                    </button>
                  )}
                </div>
              </div>

              {/* Right Pane - Raw text view */}
              <div className="glass-panel" style={{ height: 'fit-content' }}>
                <div className="pane-title">
                  <h2>Raw Document Text</h2>
                  <FileText className="text-cyan" />
                </div>
                <div className="raw-text-container">
                  {selectedResume.rawText || 'No text extracted.'}
                </div>
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}

export default App;
