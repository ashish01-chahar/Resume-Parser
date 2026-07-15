import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, PieChart, Pie, Cell, LineChart, Line, CartesianGrid } from 'recharts';
import { BarChart2 } from 'lucide-react';

const COLORS = ['#7C5CFF', '#10B981', '#38BDF8', '#F59E0B', '#EF4444', '#8B5CF6'];

export default function Analytics({ triggerNotification }) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchAnalytics = async () => {
      try {
        const res = await fetch('http://localhost:8080/api/analytics');
        if (res.ok) setData(await res.json());
      } catch (err) {
        triggerNotification('error', 'Error loading analytics');
      } finally {
        setLoading(false);
      }
    };
    fetchAnalytics();
  }, [triggerNotification]);

  if (loading) return <div style={{ padding: '2rem' }}>Loading analytics...</div>;
  if (!data) return <div style={{ padding: '2rem' }}>No data available</div>;

  return (
    <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} style={{ padding: '2rem' }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '2rem' }}>
        <div style={{ padding: '1rem', background: 'var(--primary)', borderRadius: '12px', color: '#fff' }}>
          <BarChart2 size={28} />
        </div>
        <div>
          <h1 style={{ fontSize: '2rem', marginBottom: '0.25rem' }}>Platform Analytics</h1>
          <p>Deep dive into your talent pool distribution.</p>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(450px, 1fr))', gap: '2rem' }}>
        
        {/* Top Technologies (Bar) */}
        <div className="glass-panel" style={{ height: '400px', display: 'flex', flexDirection: 'column' }}>
          <h3 style={{ marginBottom: '1.5rem' }}>Top Technologies</h3>
          <div style={{ flex: 1 }}>
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={data.topTechnologies}>
                <CartesianGrid strokeDasharray="3 3" stroke="var(--border-color)" />
                <XAxis dataKey="name" stroke="var(--text-secondary)" />
                <YAxis stroke="var(--text-secondary)" />
                <Tooltip cursor={{ fill: 'rgba(255,255,255,0.05)' }} contentStyle={{ background: 'var(--card-bg)', border: '1px solid var(--border-color)', borderRadius: '8px' }} />
                <Bar dataKey="value" fill="var(--primary)" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Experience Distribution (Pie) */}
        <div className="glass-panel" style={{ height: '400px', display: 'flex', flexDirection: 'column' }}>
          <h3 style={{ marginBottom: '1.5rem' }}>Experience Distribution</h3>
          <div style={{ flex: 1 }}>
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie data={data.experienceDistribution} cx="50%" cy="50%" innerRadius={80} outerRadius={120} paddingAngle={5} dataKey="value">
                  {data.experienceDistribution.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip contentStyle={{ background: 'var(--card-bg)', border: '1px solid var(--border-color)', borderRadius: '8px' }} />
              </PieChart>
            </ResponsiveContainer>
          </div>
          {/* Custom Legend */}
          <div style={{ display: 'flex', justifyContent: 'center', gap: '1.5rem', flexWrap: 'wrap', marginTop: '1rem' }}>
             {data.experienceDistribution.map((entry, idx) => (
                <div key={idx} style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.85rem' }}>
                  <div style={{ width: '12px', height: '12px', borderRadius: '50%', background: COLORS[idx % COLORS.length] }}></div>
                  {entry.name}
                </div>
             ))}
          </div>
        </div>

        {/* Uploads Trend (Line) */}
        <div className="glass-panel" style={{ height: '400px', display: 'flex', flexDirection: 'column', gridColumn: '1 / -1' }}>
          <h3 style={{ marginBottom: '1.5rem' }}>Monthly Upload Trend</h3>
          <div style={{ flex: 1 }}>
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={data.monthlyUploads}>
                <CartesianGrid strokeDasharray="3 3" stroke="var(--border-color)" />
                <XAxis dataKey="name" stroke="var(--text-secondary)" />
                <YAxis stroke="var(--text-secondary)" />
                <Tooltip contentStyle={{ background: 'var(--card-bg)', border: '1px solid var(--border-color)', borderRadius: '8px' }} />
                <Line type="monotone" dataKey="uploads" stroke="var(--accent)" strokeWidth={3} dot={{ r: 6, fill: 'var(--accent)' }} />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </div>

      </div>
    </motion.div>
  );
}
