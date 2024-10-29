"use client";

import { usePathname } from "next/navigation";
import { Header } from "./_components/Header";
import { Footer } from "./_components/Footer";

const noHeaderFooterPaths = ['/login', '/register', '/dashboard'];

export default function ClientLayout({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const showHeaderFooter = !noHeaderFooterPaths.includes(pathname);

  return (
    <>
      {showHeaderFooter && <Header />}
      {children}
      {showHeaderFooter && <Footer />}
    </>
  );
}