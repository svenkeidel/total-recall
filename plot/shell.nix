{ pkgs ? import <nixpkgs> {} }:

pkgs.python3.withPackages (ps: with ps; [
  numpy
  pandas
  seaborn
  jupyter
])