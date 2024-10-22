"use client"

import { useState, useEffect } from "react"
import { Disclosure, DisclosureButton, DisclosurePanel } from "@headlessui/react"
import { Bars3Icon, XMarkIcon } from "@heroicons/react/24/outline"
import Link from "next/link"
import { usePathname } from "next/navigation"
import Image from "next/image"
import { Inter } from 'next/font/google';
import { Icon } from '@iconify/react';

const interM = Inter({
  subsets: ['latin'],
  weight: '500',
})

const interR = Inter({
  subsets: ['latin'],
  weight: '400',
})

const navigation = [
  { name: "Home", href: "/" },
  { name: "Catalog", href: "/catalog" },
  { name: "Article", href: "/article" },
  { name: "Discuss", href: "/discuss" },
  { name: "Favorites", href: "/favorites" },
  { name: "Cart", href: "/cart" },
  { name: "Profile", href: "/profile" }
];

export default function Header() {
  const pathname = usePathname();
  const [showHeader, setShowHeader] = useState(true);
  const [lastScrollY, setLastScrollY] = useState(0);

  const controlHeader = () => {
    if (typeof window !== 'undefined') {
      if (window.scrollY > lastScrollY) {
        setShowHeader(false);
      } else {
        setShowHeader(true);
      }
      setLastScrollY(window.scrollY);
    }
  };

  useEffect(() => {
    if (typeof window !== 'undefined') {
      window.addEventListener('scroll', controlHeader);

      return () => {
        window.removeEventListener('scroll', controlHeader);
      };
    }
  }, [lastScrollY]);

  return (
    <Disclosure as="nav" className={`z-100 sticky top-0 bg-white transition-transform duration-300 ${interM.className} ${showHeader ? 'translate-y-0' : '-translate-y-full'}`}>
      {({ open }) => (
        <>
          <div className="z-100 mx-auto max-w-[95%] px-2 sm:px-6 lg:px-8">
            <div className="relative flex items-center justify-between">
              <div className="absolute inset-y-0 left-0 flex items-center xl:hidden">
                <DisclosureButton className="relative inline-flex items-center justify-center rounded-md p-2 text-black hover:bg-[#d4e0c5] hover:text-black focus:outline-none focus:ring-2 focus:ring-inset focus:ring-[#85A460]">
                  <span className="absolute -inset-0.5" />
                  <span className="sr-only">Open main menu</span>
                  {open ? (
                    <XMarkIcon className="block h-6 w-6" aria-hidden="true" />
                  ) : (
                    <Bars3Icon className="block h-6 w-6" aria-hidden="true" />
                  )}
                </DisclosureButton>
              </div>
              <div className="flex flex-1 items-center justify-center xl:items-stretch xl:justify-start">
                <div className="flex flex-shrink-0 items-center">
                  <Image src="/logoBB.jpg" alt="Logo" width={150} height={500} className="ml-16 my-4"/>
                </div>
                <div className="hidden lg:ml-auto mr-8 xl:block my-auto">
                  <div className="flex space-x-12">
                    <div className="relative">
                      <Icon icon="mynaui:search" className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" width={20} height={20} />
                      <input
                        type="text"
                        className={`block w-full pl-10 pr-24 py-3 border border-blue-600 rounded-3xl shadow-sm placeholder-gray-400 sm:text-sm ${interR.className}`}
                        placeholder="Search"
                      />
                    </div>
                    {navigation.map((item) => {
                      const isActive = pathname.endsWith(item.href);
                      if (item.name === "Favorites") {
                        return (
                          <Link key={item.name} href={item.href} className="relative flex items-center">
                            <div className="flex items-center justify-center w-8 h-8 bg-dgreen rounded-full text-blue-800">
                              <Icon icon="material-symbols-light:favorite-outline" width={28} height={28}/>
                            </div>
                          </Link>
                        );
                      }
                      if (item.name === "Cart") {
                        return (
                          <Link key={item.name} href={item.href} className="relative flex items-center">
                            <div className="flex items-center justify-center w-8 h-8 bg-dgreen rounded-full text-blue-800">
                              <Icon icon="ion:cart-outline" width={28} height={28}/>
                            </div>
                          </Link>
                        );
                      }
                      if (item.name === "Profile") {
                        return (
                          <Link key={item.name} href={item.href} className="relative flex items-center">
                            <div className="flex items-center justify-center w-8 h-8 bg-dgreen rounded-full text-blue-800">
                              <Icon icon="carbon:user" width={28} height={28}/>
                            </div>
                          </Link>
                        );
                      }
                      return (
                            <Link
                              key={item.name}
                              href={item.href}
                              className={`${isActive ? "opacity-100 " : "opacity-30 hover:opacity-100 hover:underline-offset-8 hover:underline text-nowrap"} text-[#0F4A99] text-base block mx-3 transition-all my-auto`}
                            >
                              {item.name}
                            </Link>
                      );
                    })}
                  </div>
                </div>
              </div>
            </div>
          </div>

          <DisclosurePanel className="xl:hidden">
            <div className="space-y-1 px-2 pt-2 pb-3 sm:px-3">
              {navigation.map((item) => (
                <DisclosureButton
                  key={item.name}
                  as={Link}
                  href={item.href}
                  className={`${
                    pathname.endsWith(item.href)
                      ? "bg-dgreen text-white rounded-lg border-green-900"
                      : "text-gray-500 border-green-800 hover:text-black lg:border-0 max-lg:active:text-black text-nowrap"
                  } text-2xl block px-3 py-2 mx-3 transition-all lg:p-1`}
                >
                  {item.name}
                </DisclosureButton>
              ))}
            </div>
          </DisclosurePanel>
        </>
      )}
    </Disclosure>
  );
}

