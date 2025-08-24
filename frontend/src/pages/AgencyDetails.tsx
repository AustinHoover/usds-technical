import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { buildApiUrl } from '../config/api';

interface AgencyModel {
  id: number;
  name: string;
  short_name: string;
  display_name: string;
  sortable_name: string;
  slug: string;
  parent: number | null;
}

interface AgencySummary {
  id: number;
  parent: number | null;
  display_name: string;
}

const AgencyDetails: React.FC = () => {
  const { agencyId } = useParams<{ agencyId: string }>();
  const navigate = useNavigate();
  const [agency, setAgency] = useState<AgencyModel | null>(null);
  const [parentAgency, setParentAgency] = useState<AgencyModel | null>(null);
  const [childAgencies, setChildAgencies] = useState<AgencyModel[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchAgencyData = async () => {
      if (!agencyId) return;
      
      try {
        setLoading(true);
        
        // Fetch agency details
        const agencyResponse = await fetch(buildApiUrl(`api/agencies/${agencyId}`));
        if (!agencyResponse.ok) throw new Error('Agency not found');
        
        const agencyData = await agencyResponse.json();
        setAgency(agencyData);
        
        // Fetch parent agency if exists
        if (agencyData.parent) {
          const parentResponse = await fetch(buildApiUrl(`api/agencies/${agencyData.parent}`));
          if (parentResponse.ok) {
            const parentData = await parentResponse.json();
            setParentAgency(parentData);
          }
        }
        
        // Fetch all agencies to find children
        const allResponse = await fetch(buildApiUrl('api/agencies/summary'));
        if (allResponse.ok) {
          const allSummaries = await allResponse.json();
          const childIds = allSummaries
            .filter((summary: AgencySummary) => summary.parent === agencyData.id)
            .map((summary: AgencySummary) => summary.id);
          
          // Fetch full details for child agencies
          const childAgenciesData = await Promise.all(
            childIds.map(async (childId: number) => {
              const childResponse = await fetch(buildApiUrl(`api/agencies/${childId}`));
              if (childResponse.ok) {
                return await childResponse.json();
              }
              return null;
            })
          );
          
          setChildAgencies(childAgenciesData.filter(Boolean));
        }
        
      } catch (err) {
        setError(err instanceof Error ? err.message : 'An error occurred');
      } finally {
        setLoading(false);
      }
    };

    fetchAgencyData();
  }, [agencyId]);

  if (loading) {
    return (
      <div className="agency-details-page">
        <div className="agency-details-header">
          <button className="back-button" onClick={() => navigate('/agencies')}>
            ← Back to Agencies
          </button>
          <h1>Loading...</h1>
        </div>
        <div className="loading-section">
          <p>Loading agency details...</p>
        </div>
      </div>
    );
  }

  if (error || !agency) {
    return (
      <div className="agency-details-page">
        <div className="agency-details-header">
          <button className="back-button" onClick={() => navigate('/agencies')}>
            ← Back to Agencies
          </button>
          <h1>Error</h1>
        </div>
        <div className="error-section">
          <p>Error: {error || 'Agency not found'}</p>
          <button onClick={() => window.location.reload()}>Try Again</button>
        </div>
      </div>
    );
  }

  return (
    <div className="agency-details-page">
      <div className="agency-details-header">
        <h1>{agency.display_name}</h1>
        <p className="agency-subtitle">{agency.short_name}</p>
      </div>

      <div className="agency-details-content">
        <div className="overview-card">
          <h2>Agency Overview</h2>
          <div className="overview-item">
            <span className="overview-label">ID:</span>
            <span className="overview-value">{agency.id}</span>
          </div>
          <div className="overview-item">
            <span className="overview-label">Full Name:</span>
            <span className="overview-value">{agency.name}</span>
          </div>
          <div className="overview-item">
            <span className="overview-label">Short Name:</span>
            <span className="overview-value">{agency.short_name}</span>
          </div>
        </div>

        {parentAgency && (
          <div className="parent-card">
            <h2>Parent Agency</h2>
            <div className="parent-agency">
              <h3>{parentAgency.display_name}</h3>
              <p>{parentAgency.short_name}</p>
              <button 
                className="view-parent-button"
                onClick={() => navigate(`/agencies/${parentAgency.id}`)}
              >
                View Parent Agency
              </button>
            </div>
          </div>
        )}

        {childAgencies.length > 0 && (
          <div className="children-card">
            <h2>Child Agencies ({childAgencies.length})</h2>
            <div className="children-grid">
              {childAgencies.map(child => (
                <div key={child.id} className="child-agency-card">
                  <h4>{child.display_name}</h4>
                  <p>{child.short_name}</p>
                  <button 
                    className="view-child-button"
                    onClick={() => navigate(`/agencies/${child.id}`)}
                  >
                    View Details
                  </button>
                </div>
              ))}
            </div>
          </div>
        )}

        {!parentAgency && childAgencies.length === 0 && (
          <div className="standalone-card">
            <h2>Standalone Agency</h2>
            <p>This agency has no parent or child agencies.</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default AgencyDetails;
