import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import TitleCard from '../components/TitleCard';

interface TitleSummaryEntry {
  number: number;
  name: string;
  latest_amended_on: string;
  latest_issue_date: string;
  up_to_date_as_of: string;
  reserved: boolean;
  version_count: number;
}

const Titles: React.FC = () => {
  const navigate = useNavigate();
  const [titles, setTitles] = useState<TitleSummaryEntry[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>('');

  // Fetch all titles when component mounts
  useEffect(() => {
    fetchAllTitles();
  }, []);

  const fetchAllTitles = async (): Promise<void> => {
    setIsLoading(true);
    setError('');
    
    try {
      const response = await fetch('/api/titles/summary');
      if (response.ok) {
        const data = await response.json();
        setTitles(data);
      } else {
        setError(`Error: ${response.status} ${response.statusText}`);
        setTitles([]);
      }
    } catch (error) {
      setError('Error connecting to backend: ' + (error as Error).message);
      setTitles([]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleTitleClick = (titleNumber: number) => {
    navigate(`/titles/${titleNumber}`);
  };

  return (
    <div className="titles-page">
      <div className="titles-header">
        <h1>Titles</h1>
        <p>All titles in the Code of Federal Regulations</p>
      </div>

      {isLoading && (
        <div className="loading-section">
          <p>Loading titles...</p>
        </div>
      )}

      {error && (
        <div className="error-message">
          <p>❌ {error}</p>
          <button 
            onClick={fetchAllTitles}
            className="retry-button"
          >
            Retry
          </button>
        </div>
      )}

      {!isLoading && !error && titles.length > 0 && (
        <div className="titles-grid">
          {titles.map((title) => (
            <TitleCard
              key={title.number}
              title={title}
              onClick={() => handleTitleClick(title.number)}
            />
          ))}
        </div>
      )}

      {!isLoading && !error && titles.length === 0 && (
        <div className="no-titles">
          <p>No titles found in the system.</p>
        </div>
      )}
    </div>
  );
};

export default Titles;
