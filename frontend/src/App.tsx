import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './App.css';

// Import components
import Navigation from './components/Navigation';
import Home from './pages/Home';
import Titles from './pages/Titles';
import About from './pages/About';
import TitleDetails from './pages/TitleDetails';
import Agencies from './pages/Agencies';
import AgencyDetails from './pages/AgencyDetails';

function App(): JSX.Element {
  return (
    <Router>
      <div className="App">
        <Navigation />
        <main>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/titles" element={<Titles />} />
            <Route path="/about" element={<About />} />
            <Route path="/titles/:titleNumber" element={<TitleDetails />} />
            <Route path="/agencies" element={<Agencies />} />
            <Route path="/agencies/:agencyId" element={<AgencyDetails />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;
