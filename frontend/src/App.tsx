import React, { useState } from 'react';
import './App.css';

function App(): JSX.Element {
  const [count, setCount] = useState<number>(0);
  const [apiMessage, setApiMessage] = useState<string>('');
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [scraperMessage, setScraperMessage] = useState<string>('');
  const [isScraperLoading, setIsScraperLoading] = useState<boolean>(false);

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
    <div className="App">
      <header className="App-header">
        <h1>Welcome to React</h1>
        <p>
          Edit <code>src/App.tsx</code> and save to reload.
        </p>
        
        <div className="api-section">
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
        
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>
      </header>
    </div>
  );
}

export default App;
