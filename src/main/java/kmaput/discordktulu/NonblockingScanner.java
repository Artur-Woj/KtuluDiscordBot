package kmaput.discordktulu;

import java.io.Closeable;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import reactor.core.publisher.Mono;

public class NonblockingScanner implements Closeable {
	private Scanner scanner;
	private ExecutorService executor;
	private boolean closed = false;
	
	public NonblockingScanner(Scanner scanner) {
		this.scanner = scanner;
		this.executor = Executors.newFixedThreadPool(1);
	}
	
	private <T> Mono<T> get(Supplier<T> supplier) {
		return Mono.fromFuture(CompletableFuture.supplyAsync(supplier, executor));
	}
	
	public Mono<String> nextLine() {
		return get(scanner::nextLine);
	}
	public Mono<Boolean> hasNextLine() {
		return get(scanner::hasNextLine);
	}
	
	public Mono<String> next() {
		return get(scanner::next);
	}
	public Mono<Boolean> hasNext() {
		return get(scanner::hasNext);
	}
	
	public Mono<String> next(Pattern pattern) {
		return get(() -> scanner.next(pattern));
	}
	public Mono<Boolean> hasNext(Pattern pattern) {
		return get(() -> scanner.hasNext(pattern));
	}
	
	public Mono<String> next(String pattern) {
		return get(() -> scanner.next(pattern));
	}
	public Mono<Boolean> hasNext(String pattern) {
		return get(() -> scanner.hasNext(pattern));
	}
	
	public Mono<Integer> nextInt() {
		return get(scanner::nextInt);
	}
	public Mono<Boolean> hasNextInt() {
		return get(scanner::hasNextInt);
	}
	
	public Mono<Integer> nextInt(int radix) {
		return get(() -> scanner.nextInt(radix));
	}
	public Mono<Boolean> hasNextInt(int radix) {
		return get(() -> scanner.hasNextInt(radix));
	}
	
	public Mono<Long> nextLong() {
		return get(scanner::nextLong);
	}
	public Mono<Boolean> hasNextLong() {
		return get(scanner::hasNextLong);
	}
	
	public Mono<Long> nextLong(int radix) {
		return get(() -> scanner.nextLong(radix));
	}
	public Mono<Boolean> hasNextLong(int radix) {
		return get(() -> scanner.hasNextLong(radix));
	}
	
	public Mono<Short> nextShort() {
		return get(scanner::nextShort);
	}
	public Mono<Boolean> hasNextShort() {
		return get(scanner::hasNextShort);
	}
	
	public Mono<Short> nextShort(int radix) {
		return get(() -> scanner.nextShort(radix));
	}
	public Mono<Boolean> hasNextShort(int radix) {
		return get(() -> scanner.hasNextShort(radix));
	}
	
	public Mono<Byte> nextByte() {
		return get(scanner::nextByte);
	}
	public Mono<Boolean> hasNextByte() {
		return get(scanner::hasNextByte);
	}
	
	public Mono<Byte> nextByte(int radix) {
		return get(() -> scanner.nextByte(radix));
	}
	public Mono<Boolean> hasNextByte(int radix) {
		return get(() -> scanner.hasNextByte(radix));
	}
	
	public Mono<BigInteger> nextBigInteger() {
		return get(scanner::nextBigInteger);
	}
	public Mono<Boolean> hasNextBigInteger() {
		return get(scanner::hasNextBigInteger);
	}
	
	public Mono<BigInteger> nextBigInteger(int radix) {
		return get(() -> scanner.nextBigInteger(radix));
	}
	public Mono<Boolean> hasNextBigInteger(int radix) {
		return get(() -> scanner.hasNextBigInteger(radix));
	}
	
	public Mono<Boolean> nextBoolean() {
		return get(scanner::nextBoolean);
	}
	public Mono<Boolean> hasNextBoolean() {
		return get(scanner::hasNextBoolean);
	}
	
	public Mono<BigDecimal> nextBigDecimal() {
		return get(scanner::nextBigDecimal);
	}
	public Mono<Boolean> hasNextBigDecimal() {
		return get(scanner::hasNextBigDecimal);
	}
	
	public Mono<Float> nextFloat() {
		return get(scanner::nextFloat);
	}
	public Mono<Boolean> hasNextFloat() {
		return get(scanner::hasNextFloat);
	}
	
	public Mono<Double> nextDouble() {
		return get(scanner::nextDouble);
	}
	public Mono<Boolean> hasNextDouble() {
		return get(scanner::hasNextDouble);
	}
	
	public NonblockingScanner skip(Pattern pattern) {
		scanner.skip(pattern);
		return this;
	}
	
	public NonblockingScanner skip(String pattern) {
		scanner.skip(pattern);
		return this;
	}
	
	public Mono<String> findInLine(Pattern pattern) {
		return get(() -> scanner.findInLine(pattern));
	}
	
	public Mono<String> findInLine(String pattern) {
		return get(() -> scanner.findInLine(pattern));
	}
	
	public Mono<String> findWithinHorizon(Pattern pattern, int horizon) {
		return get(() -> scanner.findWithinHorizon(pattern, horizon));
	}
	
	public Mono<String> findWithinHorizon(String pattern, int horizon) {
		return get(() -> scanner.findWithinHorizon(pattern, horizon));
	}
	
	public Pattern delimeter() {
		return scanner.delimiter();
	}
	
	public IOException ioException() {
		return scanner.ioException();
	}
	
	public Locale locale() {
		return scanner.locale();
	}
	
	public MatchResult matchResult() {
		return scanner.match();
	}
	
	public int radix() {
		return scanner.radix();
	}
	
	public NonblockingScanner reset() {
		scanner.reset();
		return this;
	}

	public NonblockingScanner useDelimiter(Pattern pattern) {
		scanner.useDelimiter(pattern);
		return this;
	}

	public NonblockingScanner useDelimiter(String pattern) {
		scanner.useDelimiter(pattern);
		return this;
	}

	public NonblockingScanner useRadix(int radix) {
		scanner.useRadix(radix);
		return this;
	}

	public NonblockingScanner useLocale(Locale locale) {
		scanner.useLocale(locale);
		return this;
	}
	
	@Override
	public void close() {
		if(closed) return;
		scanner.close();
		scanner = null;
		closed = true;
	}
}
