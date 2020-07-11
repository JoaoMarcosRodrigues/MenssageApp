package com.example.menssageapp;

public class Contato {
    String nome;
    String telefone;
    int imagem;

    public Contato(String nome, String telefone, int imagem){
        this.nome = nome;
        this.telefone = telefone;
        this.imagem = imagem;
    }

    public int getImagem() {
        return imagem;
    }

    public void setImagem(int imagem) {
        this.imagem = imagem;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}