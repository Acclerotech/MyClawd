import { Button, Layout, Menu } from 'antd';
import { useTranslation } from 'react-i18next';
import { Navigate, Route, Routes, useLocation, useNavigate } from 'react-router-dom';
import WorkOrders from './pages/WorkOrders';
import Placeholder from './pages/Placeholder';

const { Header, Sider, Content } = Layout;

export default function App() {
  const { t, i18n } = useTranslation();
  const navigate = useNavigate();
  const { pathname } = useLocation();

  const items = [
    {
      type: 'group' as const,
      key: 'uniksign',
      label: t('nav.uniksign'),
      children: [{ key: '/entity-profile', label: t('nav.entityProfile') }],
    },
    {
      type: 'group' as const,
      key: 'docutrail',
      label: t('nav.docutrail'),
      children: [
        { key: '/dashboard', label: t('nav.dashboard') },
        { key: '/work-orders', label: t('nav.workOrders') },
        { key: '/trust-audit', label: t('nav.trustAudit') },
      ],
    },
  ];

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider breakpoint="lg" collapsedWidth={0} theme="light" width={240}>
        <div style={{ padding: '20px 24px', fontWeight: 700, fontSize: 18 }}>{t('app.name')}</div>
        <Menu mode="inline" selectedKeys={[pathname]} items={items} onClick={(e) => navigate(e.key)} />
      </Sider>
      <Layout>
        <Header style={{ background: 'transparent', display: 'flex', justifyContent: 'flex-end', alignItems: 'center' }}>
          <Button onClick={() => void i18n.changeLanguage(i18n.language === 'fr' ? 'en' : 'fr')}>
            {t('language.toggle')}
          </Button>
        </Header>
        <Content style={{ padding: '0 24px 24px' }}>
          <Routes>
            <Route path="/" element={<Navigate to="/work-orders" replace />} />
            <Route path="/work-orders" element={<WorkOrders />} />
            <Route path="/dashboard" element={<Placeholder titleKey="dashboard.title" messageKey="dashboard.empty" />} />
            <Route path="/entity-profile" element={<Placeholder titleKey="nav.entityProfile" />} />
            <Route path="/trust-audit" element={<Placeholder titleKey="nav.trustAudit" />} />
          </Routes>
        </Content>
      </Layout>
    </Layout>
  );
}
