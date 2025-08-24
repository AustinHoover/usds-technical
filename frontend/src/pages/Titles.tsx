import React, { useState } from 'react';

const Titles: React.FC = () => {
  const [titleNumber, setTitleNumber] = useState<string>('');
  const [issueDates, setIssueDates] = useState<string[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string>('');

  const fetchIssueDates = async (): Promise<void> => {
    if (!titleNumber.trim()) {
      setError('Please enter a title number');
      return;
    }

    setIsLoading(true);
    setError('');
    
    try {
      const response = await fetch(`/api/titles/${titleNumber}/issue-dates`);
      if (response.ok) {
        const data = await response.json();
        setIssueDates(data);
      } else {
        setError(`Error: ${response.status} ${response.statusText}`);
        setIssueDates([]);
      }
    } catch (error) {
      setError('Error connecting to backend: ' + (error as Error).message);
      setIssueDates([]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleSubmit = (e: React.FormEvent): void => {
    e.preventDefault();
    fetchIssueDates();
  };

  return (
    <div className="titles-page">
      <h1>Title Management</h1>
      <p>Search for title information and issue dates.</p>
      
      <div className="search-section">
        <h3>Search by Title Number</h3>
        <form onSubmit={handleSubmit}>
          <div className="input-group">
            <label htmlFor="titleNumber">Title Number:</label>
            <input
              type="text"
              id="titleNumber"
              value={titleNumber}
              onChange={(e) => setTitleNumber(e.target.value)}
              placeholder="Enter title number (e.g., 1)"
              required
            />
            <button 
              type="submit" 
              disabled={isLoading || !titleNumber.trim()}
              className="search-button"
            >
              {isLoading ? 'Searching...' : 'Search'}
            </button>
          </div>
        </form>
      </div>

      {error && (
        <div className="error-message">
          <p>❌ {error}</p>
        </div>
      )}

      {issueDates.length > 0 && (
        <div className="results-section">
          <h3>Issue Dates for Title {titleNumber}</h3>
          <div className="dates-list">
            {issueDates.map((date, index) => (
              <div key={index} className="date-item">
                {date}
              </div>
            ))}
          </div>
          <p className="results-summary">
            Found {issueDates.length} unique issue date{issueDates.length !== 1 ? 's' : ''}
          </p>
        </div>
      )}

      {!isLoading && !error && issueDates.length === 0 && titleNumber && (
        <div className="no-results">
          <p>No issue dates found for title {titleNumber}</p>
        </div>
      )}
    </div>
  );
};

export default Titles;
