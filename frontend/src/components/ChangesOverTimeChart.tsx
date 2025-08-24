import React from 'react';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler,
} from 'chart.js';
import { Line } from 'react-chartjs-2';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
);

interface ChangesOverTimeChartProps {
  issueDates: string[];
  titleNumber: number;
}

const ChangesOverTimeChart: React.FC<ChangesOverTimeChartProps> = ({ issueDates, titleNumber }) => {
  // Sort dates and create data for the chart
  const sortedDates = issueDates
    .filter(date => date && date.trim() !== '')
    .sort((a, b) => new Date(a).getTime() - new Date(b).getTime());

  // Smart aggregation based on data volume
  const getAggregationStrategy = (dateCount: number) => {
    if (dateCount <= 12) return 'monthly'; // Show monthly for small datasets
    if (dateCount <= 60) return 'quarterly'; // Show quarterly for medium datasets
    if (dateCount <= 200) return 'biannual'; // Show every 6 months for larger datasets
    return 'yearly'; // Show yearly for very large datasets
  };

  const aggregateData = (dates: string[], strategy: string) => {
    const groups: Record<string, number> = {};
    
    dates.forEach(date => {
      try {
        const dateObj = new Date(date);
        const year = dateObj.getFullYear();
        let key: string;
        
        switch (strategy) {
          case 'monthly':
            key = `${year}-${dateObj.getMonth() + 1}`;
            break;
          case 'quarterly':
            const quarter = Math.floor(dateObj.getMonth() / 3) + 1;
            key = `${year}-Q${quarter}`;
            break;
          case 'biannual':
            const half = dateObj.getMonth() < 6 ? 'H1' : 'H2';
            key = `${year}-${half}`;
            break;
          case 'yearly':
            key = `${year}`;
            break;
          default:
            key = `${year}`;
        }
        
        if (!groups[key]) {
          groups[key] = 0;
        }
        groups[key]++;
      } catch (error) {
        console.warn('Invalid date format:', date);
      }
    });
    
    return groups;
  };

  const strategy = getAggregationStrategy(sortedDates.length);
  const dateGroups = aggregateData(sortedDates, strategy);

  // Create labels and data for the chart
  const labels = Object.keys(dateGroups).sort();
  const data = labels.map(key => dateGroups[key]);

  // Apply smoothing to reduce noise
  const smoothData = (data: number[], factor: number = 0.3) => {
    if (data.length <= 2) return data;
    
    const smoothed = [data[0]];
    for (let i = 1; i < data.length - 1; i++) {
      const prev = data[i - 1];
      const curr = data[i];
      const next = data[i + 1];
      const smoothedValue = curr * (1 - factor) + (prev + next) * factor * 0.5;
      smoothed.push(Math.round(smoothedValue * 100) / 100);
    }
    smoothed.push(data[data.length - 1]);
    return smoothed;
  };

  const smoothedData = smoothData(data, strategy === 'yearly' ? 0.5 : 0.3);

  const formatLabel = (label: string, strategy: string) => {
    switch (strategy) {
      case 'monthly':
        const [year, month] = label.split('-');
        return `${month}/${year}`;
      case 'quarterly':
        const [y, q] = label.split('-');
        return `${q} ${y}`;
      case 'biannual':
        const [yr, half] = label.split('-');
        return `${half} ${yr}`;
      case 'yearly':
        return label;
      default:
        return label;
    }
  };

  const getStrategyDescription = (strategy: string) => {
    switch (strategy) {
      case 'monthly': return 'Monthly';
      case 'quarterly': return 'Quarterly';
      case 'biannual': return 'Every 6 Months';
      case 'yearly': return 'Yearly';
      default: return 'Aggregated';
    }
  };

  const chartData = {
    labels: labels.map(label => formatLabel(label, strategy)),
    datasets: [
      {
        label: `Title ${titleNumber} (${getStrategyDescription(strategy)})`,
        data: smoothedData,
        borderColor: 'rgb(59, 130, 246)',
        backgroundColor: 'rgba(59, 130, 246, 0.1)',
        borderWidth: 3,
        fill: true,
        tension: 0.4,
        pointBackgroundColor: 'rgb(59, 130, 246)',
        pointBorderColor: '#fff',
        pointBorderWidth: 2,
        pointRadius: strategy === 'yearly' ? 8 : 6,
        pointHoverRadius: strategy === 'yearly' ? 10 : 8,
        pointHitRadius: 10,
      },
    ],
  };

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'top' as const,
        labels: {
          font: {
            size: 14,
            weight: 'bold' as const,
          },
          color: '#333',
        },
      },
      title: {
        display: true,
        text: `Updates Over Time`,
        font: {
          size: 18,
          weight: 'bold' as const,
        },
        color: '#333',
        padding: 20,
      },
      tooltip: {
        backgroundColor: 'rgba(0, 0, 0, 0.8)',
        titleColor: '#fff',
        bodyColor: '#fff',
        borderColor: 'rgb(59, 130, 246)',
        borderWidth: 1,
        cornerRadius: 8,
        displayColors: false,
        callbacks: {
          title: (tooltipItems: any[]) => {
            return `Period: ${tooltipItems[0].label}`;
          },
          label: (context: any) => {
            const originalValue = data[context.dataIndex];
            const smoothedValue = context.parsed.y;
            if (Math.abs(originalValue - smoothedValue) > 0.01) {
              return [
                `Smoothed: ${smoothedValue}`,
                `Actual: ${originalValue}`,
              ];
            }
            return `Changes: ${originalValue}`;
          },
        },
      },
    },
    scales: {
      x: {
        title: {
          display: true,
          text: `Time Period (${getStrategyDescription(strategy)})`,
          font: {
            size: 14,
            weight: 'bold' as const,
          },
          color: '#666',
        },
        grid: {
          color: 'rgba(0, 0, 0, 0.1)',
        },
        ticks: {
          color: '#666',
          font: {
            size: 12,
          },
          maxRotation: 45,
          minRotation: 0,
        },
      },
      y: {
        title: {
          display: true,
          text: 'Number of Changes',
          font: {
            size: 14,
            weight: 'bold' as const,
          },
          color: '#666',
        },
        grid: {
          color: 'rgba(0, 0, 0, 0.1)',
        },
        ticks: {
          color: '#666',
          font: {
            size: 12,
          },
          stepSize: Math.max(1, Math.ceil(Math.max(...data) / 10)),
          beginAtZero: true,
        },
      },
    },
    interaction: {
      intersect: false,
      mode: 'index' as const,
    },
    elements: {
      point: {
        radius: strategy === 'yearly' ? 8 : 6,
        hoverRadius: strategy === 'yearly' ? 10 : 8,
      },
    },
  };

  if (sortedDates.length === 0) {
    return (
      <div className="no-data-message">
        <p>No issue dates available to create a chart.</p>
      </div>
    );
  }

  return (
    <div className="chart-container">
      <div className="chart-wrapper">
        <Line data={chartData} options={options} height={400} />
      </div>
      <div className="chart-summary">
        <div>
          <strong>Total Changes:</strong> {sortedDates.length} over{' '}
          {labels.length} time period{labels.length !== 1 ? 's' : ''}
        </div>
        <div>
          <strong>Aggregation:</strong> {getStrategyDescription(strategy)} view for better visualization
        </div>
        <div>
          <strong>Date Range:</strong> {new Date(sortedDates[0]).toLocaleDateString()} to{' '}
          {new Date(sortedDates[sortedDates.length - 1]).toLocaleDateString()}
        </div>
      </div>
    </div>
  );
};

export default ChangesOverTimeChart;
