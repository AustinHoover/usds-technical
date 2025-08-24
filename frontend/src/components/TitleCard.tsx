import React, { useEffect, useState } from 'react';
import { TitleSummaryEntry } from '../pages/Titles';

interface TitleCardProps {
  title: TitleSummaryEntry;
  onClick: () => void;
}

interface AdvancedStats {
  size: number;
}

const TitleCard: React.FC<TitleCardProps> = ({ title, onClick }) => {
  const [advancedStats, setAdvancedStats] = useState<AdvancedStats | null>(null);
  const [statsLoading, setStatsLoading] = useState(false);

  // Hardcoded size thresholds in bytes
  const SMALL_SIZE_THRESHOLD = 5000000;    // 5MB
  const MEDIUM_SIZE_THRESHOLD = 20000000;  // 10MB

  // Hardcoded version count thresholds
  const RARE_VERSION_THRESHOLD = 50;        // 10 versions
  const OCCASIONAL_VERSION_THRESHOLD = 300; // 10-100 versions

  const getSizeCategory = (size: number): { label: string; className: string } => {
    if (size <= SMALL_SIZE_THRESHOLD) {
      return { label: 'Small', className: 'size-badge-small' };
    } else if (size <= MEDIUM_SIZE_THRESHOLD) {
      return { label: 'Medium', className: 'size-badge-medium' };
    } else {
      return { label: 'Large', className: 'size-badge-large' };
    }
  };

  const getVersionCategory = (versionCount: number): { label: string; className: string } => {
    if (versionCount <= RARE_VERSION_THRESHOLD) {
      return { label: 'Rare Edits', className: 'version-badge-rare' };
    } else if (versionCount <= OCCASIONAL_VERSION_THRESHOLD) {
      return { label: 'Occasional Edits', className: 'version-badge-occasional' };
    } else {
      return { label: 'Frequent Edits', className: 'version-badge-frequent' };
    }
  };

  useEffect(() => {
    const fetchAdvancedStats = async () => {
      setStatsLoading(true);
      try {
        const response = await fetch(`http://localhost:8080/api/titles/${title.number}/advanced-stats`);
        if (response.ok) {
          const stats = await response.json();
          setAdvancedStats(stats);
        }
      } catch (error) {
        console.warn('Failed to fetch advanced stats for title', title.number, error);
      } finally {
        setStatsLoading(false);
      }
    };

    fetchAdvancedStats();
  }, [title.number]);

  const versionCategory = getVersionCategory(title.version_count);

  return (
    <div className="title-card" onClick={onClick}>
      <div className="title-header">
        <div className="title-number">Title {title.number} - {title.name}</div>
        {title.reserved && <div className="reserved-badge">Reserved</div>}
      </div>
      <div className="title-content">
        <div className="title-badges">
          <div className={`version-count-badge ${versionCategory.className}`}>
            {versionCategory.label}
          </div>
          {advancedStats && (
            <div className={`size-badge ${getSizeCategory(advancedStats.size).className}`}>
              {getSizeCategory(advancedStats.size).label}
            </div>
          )}
          {statsLoading && (
            <div className="stats-loading">
              Loading stats...
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default TitleCard;
