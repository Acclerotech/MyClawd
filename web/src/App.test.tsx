import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { act, cleanup, fireEvent, render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import i18n from './i18n';
import App from './App';

// antd reads matchMedia; jsdom does not provide it.
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: (query: string) => ({
    matches: false, media: query, onchange: null,
    addListener: vi.fn(), removeListener: vi.fn(),
    addEventListener: vi.fn(), removeEventListener: vi.fn(), dispatchEvent: vi.fn(),
  }),
});

function renderApp() {
  return render(
    <MemoryRouter initialEntries={['/work-orders']}>
      <App />
    </MemoryRouter>,
  );
}

describe('App shell', () => {
  afterEach(cleanup);

  beforeEach(async () => {
    await act(() => i18n.changeLanguage('en'));
    vi.stubGlobal('fetch', vi.fn(async () => new Response(JSON.stringify([
      { id: 1, reference: 'WO-001', clientEntityId: 1, providerEntityId: 2, currentStep: 3, createdAt: '2026-10-30T09:00:00Z' },
    ]))));
  });

  it('lists work orders from the gateway', async () => {
    renderApp();
    expect(await screen.findByText('WO-001')).toBeInTheDocument();
    expect(screen.getByText('Step 3 of 7')).toBeInTheDocument();
  });

  it('switches the whole UI to French, including mixed text', async () => {
    renderApp();
    await screen.findByText('WO-001');
    fireEvent.click(screen.getByRole('button', { name: 'Français' }));
    expect(await screen.findByText('Étape 3 sur 7')).toBeInTheDocument();
    expect(screen.getAllByText('Ordres de travail').length).toBeGreaterThan(0);
  });
});
