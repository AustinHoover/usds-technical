import React from 'react';
import { Link } from 'react-router-dom';

const Home: React.FC = () => {
  return (
    <div className="home-page">
      <div className="home-header">
        <h1>Welcome to CFR Visualizer</h1>
        <p>Explore federal regulations and agencies</p>
      </div>
      
      <div className="home-content">
        <div className="navigation-cards">
          <Link to="/titles" className="nav-card">
            <div className="nav-card-content">
              <h2>📚 Titles</h2>
              <p>Browse and explore federal regulation titles</p>
              <span className="nav-link-text">View Titles →</span>
            </div>
          </Link>
          
          <Link to="/agencies" className="nav-card">
            <div className="nav-card-content">
              <h2>🏛️ Agencies</h2>
              <p>Explore federal government agencies and their hierarchy</p>
              <span className="nav-link-text">View Agencies →</span>
            </div>
          </Link>
        </div>
      </div>
    </div>
  );
};

export default Home;
