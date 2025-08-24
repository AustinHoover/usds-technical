import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import ChangesOverTimeChart from '../components/ChangesOverTimeChart';

interface TitleSummaryEntry {
  number: number;
  name: string;
  latest_amended_on: string;
  latest_issue_date: string;
  up_to_date_as_of: string;
  reserved: boolean;
  version_count: number;
}

const TitleDetails: React.FC = () => {
  const { titleNumber } = useParams<{ titleNumber: string }>();
  const navigate = useNavigate();
  const [title, setTitle] = useState<TitleSummaryEntry | null>(null);
  const [issueDates, setIssueDates] = useState<string[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>('');

  useEffect(() => {
    if (titleNumber) {
      fetchTitleDetails();
      fetchIssueDates();
    }
  }, [titleNumber]);

  const fetchTitleDetails = async (): Promise<void> => {
    try {
      const response = await fetch('/api/titles/summary');
      if (response.ok) {
        const data = await response.json();
        const foundTitle = data.find((t: TitleSummaryEntry) => t.number.toString() === titleNumber);
        if (foundTitle) {
          setTitle(foundTitle);
        } else {
          setError('Title not found');
        }
      } else {
        setError(`Error: ${response.status} ${response.statusText}`);
      }
    } catch (error) {
      setError('Error connecting to backend: ' + (error as Error).message);
    }
  };

  const fetchIssueDates = async (): Promise<void> => {
    try {
      const response = await fetch(`/api/titles/${titleNumber}/issue-dates`);
      if (response.ok) {
        const data = await response.json();
        setIssueDates(data);
      } else {
        setError(`Error fetching issue dates: ${response.status} ${response.statusText}`);
      }
    } catch (error) {
      setError('Error connecting to backend: ' + (error as Error).message);
    } finally {
      setIsLoading(false);
    }
  };

  const formatDate = (dateString: string): string => {
    if (!dateString) return 'N/A';
    try {
      const date = new Date(dateString);
      return date.toLocaleDateString();
    } catch {
      return dateString;
    }
  };

  if (isLoading) {
    return (
      <div className="title-details-page">
        <div className="loading-section">
          <p>Loading title details...</p>
        </div>
      </div>
    );
  }

  if (error || !title) {
    return (
      <div className="title-details-page">
        <div className="error-section">
          <h1>Error</h1>
          <p>{error || 'Title not found'}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="title-details-page">
      <div className="title-details-header">
        <h1>{title.name}</h1>
        <p className="title-subtitle">Title {title.number}</p>
      </div>

      <div className="title-details-content">
        <div className="title-overview"
          style={{
            width: '33%',
          }}
        >
          <div className="overview-card">
            <h3>Overview</h3>
            <div className="overview-item">
              <span className="label">Title Number:</span>
              <span className="value">{title.number}</span>
            </div>
            <div className="overview-item">
              <span className="label">Name:</span>
              <span className="value">{title.name || 'No name available'}</span>
            </div>
            <div className="overview-item">
              <span className="label">Status:</span>
              <span className="value">
                {title.reserved ? (
                  <span className="reserved-badge">Reserved</span>
                ) : (
                  <span className="active-badge">Active</span>
                )}
              </span>
            </div>
            <div className="overview-item">
              <span className="label">Total Versions:</span>
              <span className="value">{title.version_count}</span>
            </div>
            <div className="date-item">
              <span className="date-label">Latest Issue Date:</span>
              <span className="date-value">{formatDate(title.latest_issue_date)}</span>
            </div>
            <div className="date-item">
              <span className="date-label">Latest Amendment:</span>
              <span className="date-value">{formatDate(title.latest_amended_on)}</span>
            </div>
            <div className="date-item">
              <span className="date-label">Up to Date As Of:</span>
              <span className="date-value">{formatDate(title.up_to_date_as_of)}</span>
            </div>
          </div>
        </div>

        {issueDates.length > 0 && (
          <div className="changes-chart-section"
          style={{
            width: '33%',
          }}
          >
            <div className="chart-card">
              <h3>Changes Over Time</h3>
              <ChangesOverTimeChart 
                issueDates={issueDates} 
                titleNumber={title.number} 
              />
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default TitleDetails;
