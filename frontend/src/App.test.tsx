import { render, screen } from '@testing-library/react';
import App from './App';

test('renders welcome message', (): void => {
  render(<App />);
  const linkElement = screen.getByText(/Welcome to React/i);
  expect(linkElement).toBeInTheDocument();
});

test('renders counter', (): void => {
  render(<App />);
  const counterElement = screen.getByText(/Counter: 0/i);
  expect(counterElement).toBeInTheDocument();
});

test('renders scraper section', (): void => {
  render(<App />);
  const scraperElement = screen.getByText(/Data Scraper/i);
  expect(scraperElement).toBeInTheDocument();
});
