package src;

/**
 * @author Daniel Valadares, Gustavo Silvestre e Larissa Valadares
 */

import java.util.Arrays;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Conta {
    // dados
    private int idConta;
    private String nomePessoa;
    private String[] email;
    private String nomeUsuario;
    private String senha;
    private String cpf;
    private String cidade;
    private int transferenciasRealizadas;
    private Float saldoConta;

    // gets & sets
    public int getIdConta() {
        return this.idConta;
    }

    public void setIdConta(int idConta) {
        this.idConta = idConta;
    }

    public String getNomePessoa() {
        return this.nomePessoa;
    }

    public void setNomePessoa(String nomePessoa) {
        this.nomePessoa = nomePessoa;
    }

    public String[] getEmail() {
        return this.email;
    }

    public void setEmail(String[] email) {
        this.email = email;
    }

    public String getNomeUsuario() {
        return this.nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getSenha() {
        return this.senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getsenha() {
        return this.senha;
    }

    public void setsenha(String senha) {
        this.senha = senha;
    }

    public String getCpf() {
        return this.cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCidade() {
        return this.cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public int getTransferenciasRealizadas() {
        return this.transferenciasRealizadas;
    }

    public void setTransferenciasRealizadas(int transferenciasRealizadas) {
        this.transferenciasRealizadas = transferenciasRealizadas;
    }

    public Float getSaldoConta() {
        return this.saldoConta;
    }

    public void setSaldoConta(Float saldoConta) {
        this.saldoConta = saldoConta;
    }

    // Construtores
    public Conta() {
        this.idConta = -2147483648;
        this.nomePessoa = null;
        this.email = null;
        this.nomeUsuario = null;
        this.senha = null;
        this.cpf = null;
        this.cidade = null;
        this.transferenciasRealizadas = -2147483648;
        this.saldoConta = null;
    }

    public Conta(int idConta, String nomePessoa, String[] email, String nomeUsuario, String senha, String cpf,
            String cidade, int transferenciasRealizadas, Float saldoConta) {
        this.idConta = idConta;
        this.nomePessoa = nomePessoa;
        this.email = email;
        this.nomeUsuario = nomeUsuario;
        this.senha = senha;
        this.cpf = cpf;
        this.cidade = cidade;
        this.transferenciasRealizadas = transferenciasRealizadas;
        this.saldoConta = saldoConta;
    }

    // Clone

    public Conta clone() {
        Conta tmp = new Conta();
        tmp.idConta = this.idConta;
        tmp.nomePessoa = this.nomePessoa;
        tmp.email = this.email;
        tmp.nomeUsuario = this.nomeUsuario;
        tmp.senha = this.senha;
        tmp.cpf = this.cpf;
        tmp.cidade = this.cidade;
        tmp.transferenciasRealizadas = this.transferenciasRealizadas;
        tmp.saldoConta = this.saldoConta;
        return tmp;
    }

    // Metodos
    public String toString() {
        return idConta + " | " + nomePessoa + " | " + Arrays.toString(email) + " | " + nomeUsuario + " | " + senha
                + " | "
                + cpf + " | " + cidade + " | " + transferenciasRealizadas + " | " + saldoConta;
    }

    public void transfer(Float amount, Conta receiver) {
        if (this.saldoConta >= amount) {
            // dados
            Conta thisBackup = this.clone();
            Conta receiverBackup = receiver.clone();
            // realizar transferencia
            this.saldoConta -= amount;
            receiver.setSaldoConta(receiver.getSaldoConta() + amount);
            this.transferenciasRealizadas++;
            receiver.setTransferenciasRealizadas(receiver.getTransferenciasRealizadas() + 1);
            // update
            ContaDAO.update(thisBackup, this);
            ContaDAO.update(receiverBackup, receiver);
        } else {
            return;
        }
    }

    public byte[] toByteArray() throws IOException {
        // inicializacoes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        // dados
        int i = 0;
        // escrever
        dos.writeBoolean(false); // lapide
        dos.writeInt(this.idConta);
        dos.writeUTF(this.nomePessoa);
        dos.writeShort(this.email.length);
        do {
            dos.writeUTF(email[i]);
        } while (++i < this.email.length);
        dos.writeUTF(this.nomeUsuario);
        dos.writeUTF(this.senha);
        dos.writeUTF(this.cpf);
        dos.writeUTF(this.cidade);
        dos.writeInt(this.transferenciasRealizadas);
        dos.writeFloat(this.saldoConta);
        // return
        return baos.toByteArray();
    }
}
