import React from 'react';

interface TitleSummaryEntry {
  number: number;
  name: string;
  latest_amended_on: string;
  latest_issue_date: string;
  up_to_date_as_of: string;
  reserved: boolean;
  version_count: number;
}

interface TitleCardProps {
  title: TitleSummaryEntry;
  onClick?: () => void;
}

const TitleCard: React.FC<TitleCardProps> = ({ title, onClick }) => {
  const formatDate = (dateString: string): string => {
    if (!dateString) return 'N/A';
    try {
      const date = new Date(dateString);
      return date.toLocaleDateString();
    } catch {
      return dateString;
    }
  };

  return (
    <div 
      className={`title-card ${onClick ? 'clickable' : ''}`}
      onClick={onClick}
    >
      <div className="title-header">
        <h3 className="title-number">Title {title.number}</h3>
        <div className="title-badges">
          {title.reserved && (
            <span className="reserved-badge">Reserved</span>
          )}
          <span className="version-count-badge">
            {title.version_count} Version{title.version_count !== 1 ? 's' : ''}
          </span>
        </div>
      </div>
      
      <div className="title-content">
        <p className="title-name">{title.name || 'No name available'}</p>
        
        <div className="title-dates">
          <div className="date-item">
            <span className="date-label">Latest Issue:</span>
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
    </div>
  );
};

export default TitleCard;
