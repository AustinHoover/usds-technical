import React, { useState, useEffect } from 'react';

const Home: React.FC = () => {
  const [count, setCount] = useState<number>(0);
  const [apiMessage, setApiMessage] = useState<string>('');
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [scraperMessage, setScraperMessage] = useState<string>('');
  const [isScraperLoading, setIsScraperLoading] = useState<boolean>(false);
  
  // New state for title 1 issue dates
  const [title1IssueDates, setTitle1IssueDates] = useState<string[]>([]);
  const [title1Loading, setTitle1Loading] = useState<boolean>(true);
  const [title1Error, setTitle1Error] = useState<string>('');

  // Fetch issue dates for title "1" when component mounts
  useEffect(() => {
    fetchTitle1IssueDates();
  }, []);

  const fetchTitle1IssueDates = async (): Promise<void> => {
    setTitle1Loading(true);
    setTitle1Error('');
    
    try {
      const response = await fetch('/api/titles/1/issue-dates');
      if (response.ok) {
        const data = await response.json();
        setTitle1IssueDates(data);
      } else {
        setTitle1Error(`Error: ${response.status} ${response.statusText}`);
        setTitle1IssueDates([]);
      }
    } catch (error) {
      setTitle1Error('Error connecting to backend: ' + (error as Error).message);
      setTitle1IssueDates([]);
    } finally {
      setTitle1Loading(false);
    }
  };

  const testApiConnection = async (): Promise<void> => {
    setIsLoading(true);
    try {
      const response = await fetch('/api/hello');
      const data = await response.text();
      setApiMessage(data);
    } catch (error) {
      setApiMessage('Error connecting to backend: ' + (error as Error).message);
    } finally {
      setIsLoading(false);
    }
  };

  const triggerScraper = async (): Promise<void> => {
    setIsScraperLoading(true);
    try {
      const response = await fetch('/api/scraper/trigger', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
      });
      const data = await response.text();
      if (response.ok) {
        setScraperMessage(`✅ ${data}`);
      } else {
        setScraperMessage(`❌ Error: ${data}`);
      }
    } catch (error) {
      setScraperMessage('❌ Error connecting to scraper endpoint: ' + (error as Error).message);
    } finally {
      setIsScraperLoading(false);
    }
  };

  return (
    <div className="home-page">
      <h1>Welcome to USDS Technical</h1>
      <p>This is the home page of your application.</p>
      
      {/* Title 1 Issue Dates Section */}
      <div className="title1-section">
        <h3>Title 1 Issue Dates</h3>
        {title1Loading ? (
          <p>Loading issue dates for Title 1...</p>
        ) : title1Error ? (
          <div className="error-message">
            <p>❌ {title1Error}</p>
            <button 
              onClick={fetchTitle1IssueDates}
              className="retry-button"
            >
              Retry
            </button>
          </div>
        ) : (
          <div className="title1-results">
            <p className="title1-count">
              <strong>Total Issue Dates: {title1IssueDates.length}</strong>
            </p>
            {title1IssueDates.length > 0 && (
              <div className="title1-dates">
                <p>Issue Dates:</p>
                <div className="dates-grid">
                  {title1IssueDates.map((date, index) => (
                    <span key={index} className="date-badge">
                      {date}
                    </span>
                  ))}
                </div>
              </div>
            )}
            <button 
              onClick={fetchTitle1IssueDates}
              className="refresh-button"
            >
              Refresh Data
            </button>
          </div>
        )}
      </div>
      
      <div className="api-section">
        <h3>Backend Connection Test</h3>
        <button 
          className="api-button"
          onClick={testApiConnection}
          disabled={isLoading}
        >
          {isLoading ? 'Testing...' : 'Test Backend Connection'}
        </button>
        {apiMessage && (
          <div className="api-response">
            <p><strong>Backend Response:</strong></p>
            <p>{apiMessage}</p>
          </div>
        )}
      </div>

      <div className="scraper-section">
        <h3>Data Scraper</h3>
        <button 
          className="scraper-button"
          onClick={triggerScraper}
          disabled={isScraperLoading}
        >
          {isScraperLoading ? 'Running Scraper...' : 'Trigger Scraper Now'}
        </button>
        {scraperMessage && (
          <div className="scraper-response">
            <p><strong>Scraper Response:</strong></p>
            <p>{scraperMessage}</p>
          </div>
        )}
      </div>

      <div className="counter-section">
        <h3>Counter Demo</h3>
        <p>Counter: {count}</p>
        <button 
          className="counter-button"
          onClick={() => setCount(count + 1)}
        >
          Increment
        </button>
        <button 
          className="counter-button"
          onClick={() => setCount(count - 1)}
        >
          Decrement
        </button>
      </div>
    </div>
  );
};

export default Home;
