import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

interface AgencySummary {
  id: number;
  parent: number | null;
  display_name: string;
}

interface AgencyCardProps {
  agency: AgencySummary;
  childAgencies: AgencySummary[];
  onViewDetails: (agencyId: number) => void;
}

const AgencyCard: React.FC<AgencyCardProps> = ({ agency, childAgencies, onViewDetails }) => {
  const [isExpanded, setIsExpanded] = useState(false);

  const toggleExpanded = () => {
    setIsExpanded(!isExpanded);
  };

  return (
    <div className="agency-card">
      <div className="agency-header">
        <div className="agency-info"
        onClick={() => onViewDetails(agency.id)}
        >
          <h3 className="agency-name">{agency.display_name}</h3>
        </div>
        <div className="agency-actions">
          { 
            childAgencies.length > 0 &&
            <button 
              className="expand-button"
              onClick={toggleExpanded}
              aria-label={isExpanded ? 'Collapse' : 'Expand'}
            >
              {isExpanded ? '▼' : '▶'}
            </button>
          }
        </div>
      </div>
      
      {childAgencies.length > 0 && (
        <div className="child-count">
          {childAgencies.length} child agenc{childAgencies.length !== 1 ? 'ies' : 'y'}
        </div>
      )}
      
      {isExpanded && childAgencies.length > 0 && (
        <div className="child-agencies">
          <h4>Child Agencies:</h4>
          <div className="child-agencies-list">
            {childAgencies.map(child => (
              <div key={child.id} className="child-agency-item">
                <span className="child-name">{child.display_name}</span>
                <button 
                  className="child-details-button"
                  onClick={() => onViewDetails(child.id)}
                >
                  Details
                </button>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

const Agencies: React.FC = () => {
  const navigate = useNavigate();
  const [topLevelAgencies, setTopLevelAgencies] = useState<AgencySummary[]>([]);
  const [allAgencies, setAllAgencies] = useState<AgencySummary[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchAgencies = async () => {
      try {
        setLoading(true);
        
        // Fetch top-level agencies
        const topLevelResponse = await fetch('http://localhost:8080/api/agencies/top-level');
        if (!topLevelResponse.ok) throw new Error('Failed to fetch top-level agencies');
        const topLevel = await topLevelResponse.json();
        
        // Extract just the summary fields we need
        const topLevelSummaries = topLevel.map((agency: any) => ({
          id: agency.id,
          parent: agency.parent,
          display_name: agency.display_name
        }));
        setTopLevelAgencies(topLevelSummaries);
        
        // Fetch all agencies for child lookup
        const allResponse = await fetch('http://localhost:8080/api/agencies/summary');
        if (!allResponse.ok) throw new Error('Failed to fetch all agencies');
        const allSummaries = await allResponse.json();
        setAllAgencies(allSummaries);
        
      } catch (err) {
        setError(err instanceof Error ? err.message : 'An error occurred');
      } finally {
        setLoading(false);
      }
    };

    fetchAgencies();
  }, []);

  const getChildAgencies = (parentId: number): AgencySummary[] => {
    return allAgencies.filter(agency => agency.parent === parentId);
  };

  const handleViewDetails = (agencyId: number) => {
    navigate(`/agencies/${agencyId}`);
  };

  if (loading) {
    return (
      <div className="agencies-page">
        <div className="agencies-header">
          <h1>Federal Agencies</h1>
          <p>Browse and explore federal government agencies</p>
        </div>
        <div className="loading-section">
          <p>Loading agencies...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="agencies-page">
        <div className="agencies-header">
          <h1>Federal Agencies</h1>
          <p>Browse and explore federal government agencies</p>
        </div>
        <div className="error-section">
          <p>Error: {error}</p>
          <button onClick={() => window.location.reload()}>Try Again</button>
        </div>
      </div>
    );
  }

  return (
    <div className="agencies-page">
      <div className="agencies-header">
        <h1>Federal Agencies</h1>
        <p>Browse and explore federal government agencies</p>
      </div>
      
      <div className="agencies-content">
        <div className="agencies-grid">
          {topLevelAgencies.map(agency => (
            <AgencyCard
              key={agency.id}
              agency={agency}
              childAgencies={getChildAgencies(agency.id)}
              onViewDetails={handleViewDetails}
            />
          ))}
        </div>
        
        {topLevelAgencies.length === 0 && (
          <div className="no-agencies">
            <p>No agencies found.</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default Agencies;
